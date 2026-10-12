<template>
	<view class="page">
		<view class="nav-bar" :style="{ paddingTop: (statusBarHeight + 12) + 'px' }">
			<text class="nav-back" @click="goBack">返回</text>
			<text class="nav-title">记一笔</text>
			<text class="nav-placeholder"></text>
		</view>
		<view class="form-wrap">
			<scroll-view scroll-y class="form-scroll">
				<!-- 类型切换 -->
				<view class="type-row">
					<text
						v-for="t in types"
						:key="t.value"
						class="type-btn"
						:class="{ active: type === t.value }"
						@click="switchType(t.value)"
					>
						{{ t.label }}
					</text>
				</view>

				<!-- 分类选择 -->
				<view class="field">
					<text class="field-label">分类</text>
					<view class="picker" @click="showCategoryPopup = true">
						<text class="picker-text">{{ getCurrentCategoryName() }}</text>
						<text class="picker-arrow">∨</text>
					</view>
				</view>

				<!-- 金额 -->
				<view class="field">
					<text class="field-label">金额</text>
					<view class="amount-row">
						<text class="amount-sign">¥</text>
						<input
							v-model="amount"
							class="amount-input"
							placeholder="0.00"
							type="digit"
							@input="onAmountInput"
						/>
					</view>
				</view>

				<!-- 日期 -->
				<view class="field">
					<text class="field-label">日期</text>
					<view class="date-row">
						<text class="date-nav" @click="changeDay(-1)">‹</text>
						<picker mode="date" :value="getDateString()" :start="startDate" :end="endDate" @change="onDateChange">
							<text class="date-text">{{ getDateString() }}</text>
						</picker>
						<text class="date-nav" @click="changeDay(1)">›</text>
					</view>
				</view>

				<!-- 备注 -->
				<view class="field">
					<text class="field-label">备注</text>
					<input
						v-model="remark"
						class="remark-input"
						placeholder="添加备注..."
						:maxlength="200"
					/>
				</view>

				<button class="save-btn" :disabled="loading" @click="handleSave">
					{{ mode === 'edit' ? '保存修改' : '保存' }}
				</button>
			</scroll-view>
		</view>
	</view>

	<!-- 分类选择弹出层 -->
	<view v-if="showCategoryPopup" class="popup-overlay" @click="showCategoryPopup = false">
		<view class="popup-sheet" @click.stop>
			<view class="popup-header">
				<text class="popup-cancel" @click="showCategoryPopup = false">取消</text>
				<text class="popup-title">选择分类</text>
				<view style="width:50px"></view>
			</view>
			<scroll-view scroll-y class="popup-list">
				<view
					v-for="item in filteredCategories"
					:key="item.id"
					class="popup-item"
					:class="{ selected: item.id === selectedCategoryId }"
					@click="selectCategory(item.id)"
				>
					<text class="popup-item-text">{{ item.name }}</text>
					<text v-if="item.id === selectedCategoryId" class="popup-check">✓</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { addExpense, updateExpense, getExpenseDetail } from '@/api/expense'
import { getCategoryList } from '@/api/category'

const statusBarHeight = ref(20)
onMounted(() => {
	try {
		const sys = uni.getSystemInfoSync()
		statusBarHeight.value = sys.statusBarHeight || 20
	} catch {}
})

const types = [
	{ label: '支出', value: 0 },
	{ label: '收入', value: 1 },
]

const FALLBACK_CATEGORIES = [
	{ id: 1, name: '餐饮', type: 0 }, { id: 2, name: '交通', type: 0 },
	{ id: 3, name: '购物', type: 0 }, { id: 4, name: '娱乐', type: 0 },
	{ id: 5, name: '住房', type: 0 }, { id: 6, name: '通讯', type: 0 },
	{ id: 7, name: '医疗', type: 0 }, { id: 8, name: '教育', type: 0 },
	{ id: 9, name: '人情', type: 0 }, { id: 10, name: '其他支出', type: 0 },
	{ id: 11, name: '工资', type: 1 }, { id: 12, name: '兼职', type: 1 },
	{ id: 13, name: '红包', type: 1 }, { id: 14, name: '投资收益', type: 1 },
	{ id: 15, name: '其他收入', type: 1 },
]

const mode = ref('add')
const expenseId = ref(0)
const type = ref(0)
const categories = ref([])
const selectedCategoryId = ref(0)
const showCategoryPopup = ref(false)
const amount = ref('')
const remark = ref('')
const selectedYear = ref(2026)
const selectedMonth = ref(6)
const selectedDay = ref(1)
const loading = ref(false)

const startDate = computed(() => '2000-01-01')
const endDate = computed(() => {
	const d = new Date()
	return d.getFullYear() + '-' +
		String(d.getMonth() + 1).padStart(2, '0') + '-' +
		String(d.getDate()).padStart(2, '0')
})

onLoad((params) => {
	if (params) {
		if (params.mode === 'edit' && params.expenseId) {
			mode.value = 'edit'
			expenseId.value = parseInt(params.expenseId)
			loadExpense()
			return
		}
	}
	initDate()
	loadCategories()
})

function initDate() {
	const now = new Date()
	selectedYear.value = now.getFullYear()
	selectedMonth.value = now.getMonth() + 1
	selectedDay.value = now.getDate()
}

async function loadExpense() {
	try {
		const res = await getExpenseDetail(expenseId.value)
		if (res.code === 200 && res.data) {
			const e = res.data
			type.value = e.type
			selectedCategoryId.value = e.categoryId
			amount.value = e.amount > 0 ? e.amount.toString() : ''
			if (e.expenseDate) {
				const parts = e.expenseDate.split('-')
				if (parts.length === 3) {
					selectedYear.value = parseInt(parts[0])
					selectedMonth.value = parseInt(parts[1])
					selectedDay.value = parseInt(parts[2])
				}
			}
			remark.value = e.remark || ''
		} else {
			uni.showToast({ title: '加载失败', icon: 'none' })
			uni.navigateBack()
		}
	} catch {
		uni.showToast({ title: '加载失败', icon: 'none' })
		uni.navigateBack()
	}
	loadCategories()
}

async function loadCategories() {
	try {
		const res = await getCategoryList()
		if (res.code === 200 && res.data && res.data.length > 0) {
			categories.value = res.data
		} else {
			categories.value = FALLBACK_CATEGORIES
		}
	} catch {
		categories.value = FALLBACK_CATEGORIES
	}
	// 确保按当前类型选中第一个分类
	const arr = categories.value.filter(c => Number(c.type) === Number(type.value))
	if (arr.length > 0 && !selectedCategoryId.value) {
		selectedCategoryId.value = arr[0].id
	}
}

function switchType(val) {
	type.value = val
	const arr = categories.value.filter(c => Number(c.type) === Number(val))
	if (arr.length > 0) {
		selectedCategoryId.value = arr[0].id
	}
}

const filteredCategories = computed(() => {
	return categories.value.filter(c => Number(c.type) === Number(type.value))
})

function getCurrentCategoryName() {
	const item = categories.value.find(c => c.id === selectedCategoryId.value)
	return item ? item.name : '请选择'
}

function selectCategory(id) {
	selectedCategoryId.value = id
	showCategoryPopup.value = false
}

function onAmountInput(e) {
	let val = e.detail.value
	val = val.replace(/[^\d.]/g, '')
	const parts = val.split('.')
	let result = parts[0]
	if (parts.length > 1) {
		result += '.' + parts.slice(1).join('').slice(0, 2)
	}
	amount.value = result
}

function getDateString() {
	return selectedYear.value + '-' +
		String(selectedMonth.value).padStart(2, '0') + '-' +
		String(selectedDay.value).padStart(2, '0')
}

function changeDay(offset) {
	const d = new Date(selectedYear.value, selectedMonth.value - 1, selectedDay.value)
	d.setDate(d.getDate() + offset)
	const today = new Date()
	if (d <= today || offset < 0) {
		selectedYear.value = d.getFullYear()
		selectedMonth.value = d.getMonth() + 1
		selectedDay.value = d.getDate()
	}
}

function onDateChange(e) {
	const val = e.detail.value
	if (val) {
		const parts = val.split('-')
		if (parts.length === 3) {
			selectedYear.value = parseInt(parts[0])
			selectedMonth.value = parseInt(parts[1])
			selectedDay.value = parseInt(parts[2])
		}
	}
}

async function handleSave() {
	if (!selectedCategoryId.value) {
		uni.showToast({ title: '请选择分类', icon: 'none' })
		return
	}
	const amountNum = parseFloat(amount.value)
	if (isNaN(amountNum) || amountNum <= 0) {
		uni.showToast({ title: '请输入有效金额', icon: 'none' })
		return
	}

	loading.value = true
	const dto = {
		type: type.value,
		amount: amountNum,
		categoryId: selectedCategoryId.value,
		expenseDate: getDateString(),
		remark: remark.value.trim(),
	}

	try {
		let res
		if (mode.value === 'edit') {
			res = await updateExpense(expenseId.value, dto)
		} else {
			res = await addExpense(dto)
		}
		if (res.code === 200) {
			uni.showToast({ title: mode.value === 'edit' ? '修改成功' : '添加成功', icon: 'success' })
			uni.navigateBack()
		} else {
			uni.showToast({ title: res.message, icon: 'none' })
		}
	} catch (e) {
		uni.showToast({ title: '操作失败', icon: 'none' })
	} finally {
		loading.value = false
	}
}

function goBack() {
	uni.navigateBack()
}
</script>

<style scoped>
.page {
	background: #F5F6FA;
	min-height: 100vh;
}
.nav-bar {
	display: flex;
	align-items: center;
	padding: 12px 16px;
}
.nav-back {
	font-size: 16px;
	color: #6B7F9E;
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
.form-wrap {
	padding: 16px;
}
.form-scroll {
	max-height: calc(100vh - 80px);
}
.type-row {
	display: flex;
	padding: 8px 0;
}
.type-btn {
	font-size: 15px;
	color: #666;
	background: #F2F3F5;
	width: 100px;
	height: 40px;
	line-height: 40px;
	text-align: center;
	border-radius: 8px;
	margin-right: 12px;
	border: 1px solid transparent;
}
.type-btn.active {
	color: #fff;
	background: #6B7F9E;
	border-color: #6B7F9E;
}
.field {
	padding: 12px 0;
}
.field-label {
	font-size: 14px;
	color: #8E8E93;
	display: block;
	margin-bottom: 8px;
}
.picker {
	display: flex;
	align-items: center;
	padding: 12px 14px;
	background: #FFFFFF;
	border-radius: 10px;
	border: 1px solid #EBEDF0;
}
.picker-text {
	flex: 1;
	font-size: 16px;
	color: #333;
}
.picker-arrow {
	font-size: 12px;
	color: #C0C4CC;
}
.amount-row {
	display: flex;
	align-items: center;
	padding: 4px 14px;
	background: #FFFFFF;
	border-radius: 10px;
	border: 1px solid #EBEDF0;
}
.amount-sign {
	font-size: 24px;
	font-weight: 400;
	color: #333;
	margin-right: 8px;
}
.amount-input {
	flex: 1;
	height: 48px;
	font-size: 28px;
	font-weight: 400;
	background: transparent;
	border: none;
	outline: none;
}
.date-row {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 16px;
}
.date-nav {
	width: 36px;
	height: 36px;
	line-height: 36px;
	text-align: center;
	border-radius: 50%;
	background: #F2F3F5;
	font-size: 18px;
	font-weight: 400;
	color: #666;
}
.date-text {
	font-size: 17px;
	font-weight: 400;
	color: #333;
}
.remark-input {
	height: 48px;
	background: #FFFFFF;
	border-radius: 10px;
	padding: 0 14px;
	font-size: 15px;
	border: 1px solid #EBEDF0;
	box-sizing: border-box;
}
.save-btn {
	width: 100%;
	height: 48px;
	line-height: 48px;
	background: #6B7F9E;
	color: #fff;
	border-radius: 10px;
	font-size: 16px;
	margin-top: 32px;
	border: none;
	font-weight: 500;
	letter-spacing: 2px;
}
.save-btn[disabled] {
	opacity: 0.5;
}

/* 分类选择弹出层 */
.popup-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.4);
	z-index: 1000;
	display: flex;
	align-items: flex-end;
}
.popup-sheet {
	width: 100%;
	background: #fff;
	border-radius: 16rpx 16rpx 0 0;
	max-height: 60vh;
	display: flex;
	flex-direction: column;
}
.popup-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 16px;
	border-bottom: 1rpx solid #F0F0F0;
}
.popup-cancel {
	font-size: 16px;
	color: #6B7F9E;
	width: 50px;
}
.popup-title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
}
.popup-list {
	flex: 1;
	overflow-y: auto;
}
.popup-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 16px 20px;
	border-bottom: 1rpx solid #F5F5F5;
}
.popup-item:active {
	background: #F5F5F5;
}
.popup-item.selected {
	background: #F0F2F5;
}
.popup-item-text {
	font-size: 16px;
	color: #333;
}
.popup-check {
	font-size: 18px;
	color: #6B7F9E;
	font-weight: 500;
}
</style>
