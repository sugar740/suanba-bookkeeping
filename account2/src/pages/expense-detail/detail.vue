<template>
	<view class="page">
		<view class="nav-bar" :style="{ paddingTop: (statusBarHeight + 12) + 'px' }">
			<text class="nav-back" @click="goBack">返回</text>
			<text class="nav-title">记账详情</text>
			<text class="nav-placeholder"></text>
		</view>
		<view v-if="loading" class="loading-wrap">
			<uni-load-more status="loading" />
		</view>

		<template v-else-if="expense">
			<!-- 金额展示 -->
			<view class="amount-section">
				<text class="type-label">{{ getTypeLabel() }}</text>
				<text class="amount" :style="{ color: getTypeColor() }">{{ getAmountDisplay() }}</text>
			</view>

			<!-- 详情列表 -->
			<view class="detail-section">
				<view class="detail-row">
					<text class="detail-label">分类</text>
					<text class="detail-value">{{ getCategoryIcon() }}  {{ getCategoryName() }}</text>
				</view>
				<view class="divider"></view>
				<view class="detail-row">
					<text class="detail-label">日期</text>
					<text class="detail-value">{{ formatDate(expense.expenseDate) }}</text>
				</view>
				<view class="divider"></view>
				<view class="detail-row">
					<text class="detail-label">备注</text>
					<text class="detail-value">{{ expense.remark || '无' }}</text>
				</view>
				<view class="divider"></view>
				<view class="detail-row">
					<text class="detail-label">创建时间</text>
					<text class="detail-value">{{ formatTime(expense.createTime) }}</text>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="action-bar">
				<button class="btn-edit" @click="goEdit">编辑</button>
				<button class="btn-delete" @click="handleDelete">删除</button>
			</view>
		</template>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getExpenseDetail, deleteExpense } from '@/api/expense'

const expense = ref(null)
const loading = ref(true)

onLoad((params) => {
	if (params && params.expenseId) {
		loadDetail(parseInt(params.expenseId))
	} else {
		uni.showToast({ title: '参数错误', icon: 'none' })
		uni.navigateBack()
	}
})

async function loadDetail(id) {
	try {
		const res = await getExpenseDetail(id)
		if (res.code === 200 && res.data) {
			expense.value = res.data
		} else {
			uni.showToast({ title: res.message || '记录不存在', icon: 'none' })
			uni.navigateBack()
		}
	} catch {
		uni.showToast({ title: '加载失败', icon: 'none' })
		uni.navigateBack()
	} finally {
		loading.value = false
	}
}

function getTypeLabel() {
	return expense.value?.type === 0 ? '支出' : '收入'
}

function getTypeColor() {
	return expense.value?.type === 0 ? '#333' : '#E67E22'
}

function getAmountDisplay() {
	if (!expense.value) return ''
	const prefix = expense.value.type === 0 ? '-' : '+'
	return prefix + '¥' + Number(expense.value.amount).toFixed(2)
}

function formatDate(dateStr) {
	if (!dateStr) return ''
	const d = new Date(dateStr + 'T00:00:00')
	const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
	return d.getFullYear() + '年' + (d.getMonth() + 1) + '月' + d.getDate() + '日 ' + weekdays[d.getDay()]
}

function formatTime(dateStr) {
	if (!dateStr) return ''
	return dateStr.replace('T', ' ').slice(0, 16)
}

function getCategoryName() {
	const map = {
		1: '餐饮', 2: '交通', 3: '购物', 4: '娱乐',
		5: '住房', 6: '通讯', 7: '医疗', 8: '教育',
		9: '人情', 10: '其他支出',
		11: '工资', 12: '兼职', 13: '红包', 14: '投资收益', 15: '其他收入',
	}
	return map[expense.value?.categoryId || 0] || '其他'
}

function getCategoryIcon() {
	const name = getCategoryName()
	const icons = {
		'餐饮': '🍜', '交通': '🚌', '购物': '🛍️', '娱乐': '🎮',
		'住房': '🏠', '通讯': '📱', '医疗': '💊', '教育': '📚',
		'人情': '🧧', '其他支出': '📦',
		'工资': '💰', '兼职': '💼', '红包': '🧧', '投资收益': '📈', '其他收入': '📦',
	}
	return icons[name] || '📦'
}

function goEdit() {
	uni.navigateTo({
		url: '/pages/add-expense/add-expense?mode=edit&expenseId=' + expense.value?.id
	})
}

function handleDelete() {
	uni.showModal({
		title: '确认删除',
		content: '确定要删除这条记账记录吗？',
		success: async (res) => {
			if (res.confirm) {
				await doDelete()
			}
		}
	})
}

const statusBarHeight = ref(20)
onMounted(() => {
  try {
    const sys = uni.getSystemInfoSync()
    statusBarHeight.value = sys.statusBarHeight || 20
  } catch {}
})

function goBack() {
		uni.navigateBack()
	}

	async function doDelete() {
	if (!expense.value) return
	try {
		const res = await deleteExpense(expense.value.id)
		if (res.code === 200) {
			uni.showToast({ title: '删除成功', icon: 'success' })
			uni.navigateBack()
		} else {
			uni.showToast({ title: res.message, icon: 'none' })
		}
	} catch {
		uni.showToast({ title: '删除失败', icon: 'none' })
	}
}
</script>

<style scoped>
.page {
	background: #fff;
	min-height: 100vh;
}
.nav-bar {
	display: flex;
	align-items: center;
	padding: 12px 16px;
}
.nav-back {
	font-size: 16px;
	color: #007AFF;
	width: 50px;
}
.nav-title {
	flex: 1;
	text-align: center;
	font-size: 18px;
	font-weight: 500;
	color: #333;
}
.nav-placeholder {
	width: 50px;
}
.loading-wrap {
	display: flex;
	align-items: center;
	justify-content: center;
	padding-top: 60px;
}
.amount-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 24px 0;
	background: #FAFAFA;
}
.type-label {
	font-size: 14px;
	color: #999;
}
.amount {
	font-size: 42px;
	font-weight: bold;
	margin-top: 8px;
}
.detail-section {
	margin: 12px 16px 0;
	background: #FAFAFA;
	border-radius: 10px;
}
.detail-row {
	display: flex;
	padding: 14px 16px;
}
.detail-label {
	font-size: 15px;
	color: #666;
	width: 80px;
}
.detail-value {
	font-size: 15px;
	color: #333;
	flex: 1;
}
.divider {
	height: 1px;
	background: #F0F0F0;
	margin-left: 16px;
}
.action-bar {
	display: flex;
	justify-content: space-evenly;
	padding: 40px 16px;
	gap: 16px;
}
.btn-edit {
	flex: 1;
	height: 48px;
	line-height: 48px;
	background: #007AFF;
	color: #fff;
	border-radius: 8px;
	font-size: 16px;
	border: none;
}
.btn-delete {
	flex: 1;
	height: 48px;
	line-height: 48px;
	background: #fff;
	color: #FF3B30;
	border-radius: 8px;
	font-size: 16px;
	border: 1px solid #FF3B30;
}
</style>
