<template>
	<view class="bill-list">
		<!-- 标题行 -->
		<view class="header">
			<text class="header-title">账单</text>
			<text v-if="total > 0" class="header-count">共 {{ total }} 笔</text>
		</view>

		<!-- 筛选栏 -->
		<view class="filters">
			<text
				v-for="item in filterOptions"
				:key="item.type"
				class="filter-btn"
				:class="{ active: activeType === item.type }"
				@click="switchType(item.type)"
			>
				{{ item.label }}
			</text>
		</view>

		<!-- 列表 -->
		<scroll-view
			scroll-y
			class="list-scroll"
			@scrolltolower="onReachEnd"
		>
			<template v-if="groupedList.length > 0">
				<view v-for="group in groupedList" :key="group.date" class="day-group">
					<text class="day-label" :class="{ today: isToday(group.date) }">
						{{ getDayLabel(group.date) }}
					</text>
					<view
						v-for="item in group.items"
						:key="item.id"
						class="expense-row"
						@click="goDetail(item.id)"
					>
						<view class="expense-left">
							<view
								class="type-dot"
								:style="{ backgroundColor: item.type === 0 ? '#FF3B30' : '#34C759' }"
							></view>
							<text class="category-icon">{{ getCategoryIcon(item.categoryId) }}</text>
							<view class="expense-info">
								<text class="category-name">{{ getCategoryDisplay(item.categoryId) }}</text>
								<text v-if="item.remark" class="remark">{{ item.remark }}</text>
							</view>
						</view>
						<text
							class="amount"
							:style="{ color: item.type === 0 ? '#FF3B30' : '#34C759' }"
						>
							{{ formatAmount(item.amount, item.type) }}
						</text>
					</view>
				</view>
				<text v-if="!hasMore && groupedList.length > 0" class="no-more">— 没有更多了 —</text>
			</template>

			<template v-else-if="!loading">
				<view class="empty">
					<text class="empty-text">暂无记账记录</text>
					<text class="empty-hint">点击下方 + 记一笔</text>
				</view>
			</template>

			<uni-load-more v-if="loading" status="loading" />
		</scroll-view>

		<!-- FAB -->
		<view class="fab" @click="goAdd">
			<text class="fab-icon">+</text>
		</view>
	</view>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getExpenseList } from '@/api/expense'
import { onPageScroll } from '@dcloudio/uni-app'

const PAGE_SIZE = 20

const props = defineProps({
	refreshKey: { type: Number, default: 0 }
})

const emit = defineEmits(['update:refreshKey'])

const expenses = ref([])
const groupedList = ref([])
const activeType = ref(-1)
const currentPage = ref(1)
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)

const filterOptions = [
	{ label: '全部', type: -1 },
	{ label: '支出', type: 0 },
	{ label: '收入', type: 1 },
]

watch(() => props.refreshKey, () => {
	refresh()
})

onMounted(() => {
	loadData()
})

function loadData() {
	if (loading.value || !hasMore.value) return
	loading.value = true

	const params = {
		page: currentPage.value,
		size: PAGE_SIZE,
	}
	if (activeType.value !== -1) {
		params.type = activeType.value
	}

	getExpenseList(params).then((res) => {
		if (res.code === 200 && res.data) {
			const records = res.data.records || []
			if (currentPage.value === 1) {
				expenses.value = records
			} else {
				expenses.value = expenses.value.concat(records)
			}
			total.value = res.data.total
			hasMore.value = expenses.value.length < total.value
			groupExpenses()
		}
	}).catch((e) => {
		uni.showToast({ title: '加载失败: ' + e.message, icon: 'none' })
	}).finally(() => {
		loading.value = false
	})
}

function groupExpenses() {
	const map = {}
	for (const item of expenses.value) {
		const key = item.expenseDate
		if (!map[key]) map[key] = []
		map[key].push(item)
	}
	const sorted = Object.keys(map).sort((a, b) => b.localeCompare(a))
	groupedList.value = sorted.map(k => ({ date: k, items: map[k] }))
}

function switchType(type) {
	if (activeType.value === type) return
	activeType.value = type
	currentPage.value = 1
	hasMore.value = true
	expenses.value = []
	groupedList.value = []
	loadData()
}

function refresh() {
	currentPage.value = 1
	hasMore.value = true
	expenses.value = []
	loadData()
}

function onReachEnd() {
	if (hasMore.value && !loading.value) {
		currentPage.value++
		loadData()
	}
}

function formatAmount(amount, type) {
	const prefix = type === 0 ? '-' : '+'
	return prefix + '¥' + Number(amount).toFixed(2)
}

function getCategoryIcon(categoryId) {
	const map = {
		1: '🍜', 2: '🚌', 3: '🛍️', 4: '🎮',
		5: '🏠', 6: '📱', 7: '💊', 8: '📚',
		9: '🧧', 10: '📦',
		11: '💰', 12: '💼', 13: '🧧', 14: '📈', 15: '📦',
	}
	return map[categoryId] || '📦'
}

function getCategoryDisplay(categoryId) {
	const map = {
		1: '餐饮', 2: '交通', 3: '购物', 4: '娱乐',
		5: '住房', 6: '通讯', 7: '医疗', 8: '教育',
		9: '人情', 10: '其他支出',
		11: '工资', 12: '兼职', 13: '红包', 14: '投资收益', 15: '其他收入',
	}
	return map[categoryId] || '其他'
}

function getDayLabel(dateStr) {
	if (!dateStr) return ''
	const d = new Date(dateStr + 'T00:00:00')
	const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
	const month = (d.getMonth() + 1).toString().padStart(2, '0')
	const day = d.getDate().toString().padStart(2, '0')
	return month + '月' + day + '日 ' + weekdays[d.getDay()]
}

function getTodayStr() {
	const d = new Date()
	return d.getFullYear() + '-' +
		(d.getMonth() + 1).toString().padStart(2, '0') + '-' +
		d.getDate().toString().padStart(2, '0')
}

function isToday(dateStr) {
	return dateStr === getTodayStr()
}

function goDetail(id) {
	uni.navigateTo({ url: '/pages/expense-detail/detail?expenseId=' + id })
}

function goAdd() {
	uni.navigateTo({ url: '/pages/add-expense/add-expense?mode=add' })
}

defineExpose({ refresh })
</script>

<style scoped>
.bill-list {
	display: flex;
	flex-direction: column;
	height: 100%;
	position: relative;
}
.header {
	display: flex;
	align-items: center;
	padding: 8px 16px;
}
.header-title {
	font-size: 22px;
	font-weight: bold;
}
.header-count {
	font-size: 13px;
	color: #999;
	margin-left: auto;
}
.filters {
	display: flex;
	padding: 0 16px 8px;
}
.filter-btn {
	font-size: 14px;
	color: #666;
	background: #F5F5F5;
	padding: 6px 20px;
	border-radius: 16px;
	margin-right: 10px;
}
.filter-btn.active {
	color: #fff;
	background: #007AFF;
}
.list-scroll {
	flex: 1;
	overflow: hidden;
}
.day-group {
	margin-bottom: 2px;
}
.day-label {
	font-size: 14px;
	color: #999;
	padding: 12px 16px 6px;
	display: block;
}
.day-label.today {
	color: #007AFF;
	font-weight: 500;
}
.expense-row {
	display: flex;
	align-items: center;
	padding: 12px 16px;
	background: #fff;
}
.expense-left {
	display: flex;
	align-items: center;
	flex: 1;
}
.type-dot {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	margin-right: 10px;
}
.category-icon {
	font-size: 22px;
	margin-right: 10px;
}
.expense-info {
	display: flex;
	flex-direction: column;
}
.category-name {
	font-size: 16px;
	color: #333;
}
.remark {
	font-size: 12px;
	color: #999;
	margin-top: 2px;
	max-width: 160px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.amount {
	font-size: 17px;
	font-weight: 500;
}
.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 120px;
}
.empty-text {
	font-size: 16px;
	color: #ccc;
}
.empty-hint {
	font-size: 14px;
	color: #ddd;
	margin-top: 8px;
}
.no-more {
	font-size: 13px;
	color: #ccc;
	text-align: center;
	display: block;
	padding: 16px 0;
}
.fab {
	position: fixed;
	bottom: 30px;
	right: 24px;
	width: 56px;
	height: 56px;
	border-radius: 28px;
	background: #007AFF;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
	z-index: 10;
}
.fab-icon {
	font-size: 28px;
	color: #fff;
	line-height: 1;
}
</style>
