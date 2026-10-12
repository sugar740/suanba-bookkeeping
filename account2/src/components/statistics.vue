<template>
	<view class="statistics-page">
		<text class="page-title">统计</text>

		<view class="view-switch">
			<text
				v-for="v in views"
				:key="v.value"
				class="switch-btn"
				:class="{ active: selectedView === v.value }"
				@click="selectedView = v.value"
			>
				{{ v.label }}
			</text>
		</view>

		<!-- 分类统计 -->
		<view v-if="selectedView === 0" class="cat-stat">
			<view class="month-nav">
				<text class="nav-btn" @click="prevMonth">‹</text>
				<text class="month-label">{{ getMonthLabel() }}</text>
				<text class="nav-btn" @click="nextMonth">›</text>
			</view>
			<view class="cat-switch">
				<text
					v-for="c in catTypes"
					:key="c.value"
					class="cat-btn"
					:class="{ active: catType === c.value }"
					@click="switchCatType(c.value)"
				>
					{{ c.label }}
				</text>
			</view>

			<text v-if="catTotal > 0" class="total-amount">
				{{ catType === 0 ? '总支出' : '总收入' }}: ¥{{ catTotal.toFixed(2) }}
			</text>

			<!-- 饼图 -->
			<canvas
				v-if="showPie"
				canvas-id="pieCanvas"
				class="chart-canvas"
				width="375"
				height="280"
				@ready="drawPieChart"
			></canvas>

			<!-- 分类明细 -->
			<template v-if="categoryData.length > 0">
				<text class="section-title">分类明细</text>
				<view class="detail-list">
					<view v-for="(item, idx) in categoryData" :key="item.categoryId" class="detail-row">
						<view class="detail-left">
							<view class="color-dot" :style="{ backgroundColor: colors[idx % colors.length] }"></view>
							<text class="detail-name">{{ item.categoryName }}</text>
						</view>
						<text class="detail-amount">¥{{ Number(item.totalAmount).toFixed(2) }}</text>
						<text class="detail-pct">{{ ((item.totalAmount / catTotal) * 100).toFixed(1) }}%</text>
					</view>
				</view>
			</template>
			<view v-else-if="!loading" class="empty-stat">
				<text>该月暂无{{ catType === 0 ? '支出' : '收入' }}记录</text>
			</view>
		</view>

		<!-- 月度趋势 -->
		<view v-else class="monthly-trend">
			<text class="section-title">本年度收支趋势</text>

			<canvas
				v-if="showBar"
				canvas-id="barCanvas"
				class="chart-canvas"
				width="375"
				height="280"
				@ready="drawBarChart"
			></canvas>

			<template v-if="monthlyData.length > 0">
				<view class="detail-list">
					<view v-for="item in monthlyData" :key="item.period" class="detail-row">
						<text class="detail-name" style="width:70px">{{ item.period }}</text>
						<text class="detail-amount" style="color:#34C759;flex:1">收入: ¥{{ Math.round(item.income) }}</text>
						<text class="detail-amount" style="color:#FF3B30;flex:1">支出: ¥{{ Math.round(item.expense) }}</text>
					</view>
				</view>
			</template>
			<view v-else-if="!loading" class="empty-stat">
				<text>暂无年度数据</text>
			</view>
		</view>

		<uni-load-more v-if="loading" status="loading" />
	</view>
</template>

<script setup>
import { ref, computed, nextTick, getCurrentInstance } from 'vue'
import { categoryStatistics, monthlyStatistics } from '@/api/expense'

const instance = getCurrentInstance()

const colors = [
	'#007AFF', '#FF9500', '#FF3B30', '#34C759', '#AF52DE',
	'#FF2D55', '#5856D6', '#00C7BE', '#FF6482', '#FFD60A',
	'#32D74B', '#64D2FF'
]

const views = [
	{ label: '分类统计', value: 0 },
	{ label: '月度趋势', value: 1 },
]
const catTypes = [
	{ label: '支出', value: 0 },
	{ label: '收入', value: 1 },
]

const selectedView = ref(0)
const catType = ref(0)
const selectedMonth = ref(new Date())
const categoryData = ref([])
const monthlyData = ref([])
const catTotal = ref(0)
const loading = ref(false)
const showPie = ref(false)
const showBar = ref(false)

async function fetchData() {
	loading.value = true
	showPie.value = false
	showBar.value = false

	const y = selectedMonth.value.getFullYear()
	const m = (selectedMonth.value.getMonth() + 1).toString().padStart(2, '0')
	const startDate = y + '-' + m + '-01'
	const lastDay = new Date(y, selectedMonth.value.getMonth() + 1, 0).getDate()
	const endDate = y + '-' + m + '-' + lastDay.toString().padStart(2, '0')
	const yearStart = y + '-01-01'
	const yearEnd = y + '-12-31'

	try {
		const [catRes, monRes] = await Promise.all([
			categoryStatistics(catType.value, startDate, endDate),
			monthlyStatistics(yearStart, yearEnd)
		])

		if (catRes.code === 200 && catRes.data) {
			categoryData.value = catRes.data
			catTotal.value = catRes.data.reduce((sum, c) => sum + Number(c.totalAmount), 0)
		}
		if (monRes.code === 200 && monRes.data) {
			monthlyData.value = monRes.data
		}

		await nextTick()
		showPie.value = true
		showBar.value = true
		// 保险：延迟重绘，确保canvas已就绪
		setTimeout(() => {
			drawPieChart()
			drawBarChart()
		}, 200)
	} catch (e) {
		uni.showToast({ title: '加载统计失败', icon: 'none' })
	} finally {
		loading.value = false
	}
}

// 暴露给父组件调用
defineExpose({ fetchData })

// 默认加载 (由父组件在 onShow 时触发)
// 改为在 mounted 自动加载
import { onMounted } from 'vue'
onMounted(() => {
	fetchData()
})

function prevMonth() {
	const d = new Date(selectedMonth.value.getFullYear(), selectedMonth.value.getMonth() - 1, 1)
	selectedMonth.value = d
	fetchData()
}

function nextMonth() {
	const next = new Date(selectedMonth.value.getFullYear(), selectedMonth.value.getMonth() + 1, 1)
	if (next <= new Date()) {
		selectedMonth.value = next
		fetchData()
	}
}

function getMonthLabel() {
	return selectedMonth.value.getFullYear() + '年' + (selectedMonth.value.getMonth() + 1) + '月'
}

function switchCatType(type) {
	if (catType.value === type) return
	catType.value = type
	categoryData.value = []
	catTotal.value = 0
	fetchData()
}

function drawPieChart() {
	const ctx = uni.createCanvasContext('pieCanvas', instance.proxy)
	const canvasWidth = 375
	const canvasHeight = 280
	const cx = canvasWidth / 2
	const cy = canvasHeight / 2 - 10
	const radius = 100
	const data = categoryData.value
	const total = catTotal.value

	ctx.clearRect(0, 0, canvasWidth, canvasHeight)

	if (total === 0 || data.length === 0) {
		ctx.setFontSize(16)
		ctx.setFillStyle('#CCCCCC')
		ctx.setTextAlign('center')
		ctx.fillText('暂无数据', cx, cy)
		ctx.draw()
		return
	}

	let startAngle = -Math.PI / 2
	for (let i = 0; i < data.length; i++) {
		const item = data[i]
		const sliceAngle = (Number(item.totalAmount) / total) * 2 * Math.PI

		ctx.beginPath()
		ctx.moveTo(cx, cy)
		ctx.arc(cx, cy, radius, startAngle, startAngle + sliceAngle)
		ctx.closePath()
		ctx.setFillStyle(colors[i % colors.length])
		ctx.fill()

		const midAngle = startAngle + sliceAngle / 2
		const labelRadius = radius * 0.65
		const lx = cx + Math.cos(midAngle) * labelRadius
		const ly = cy + Math.sin(midAngle) * labelRadius
		const pct = ((Number(item.totalAmount) / total) * 100).toFixed(1)

		ctx.setFontSize(12)
		ctx.setFillStyle('#FFFFFF')
		ctx.setTextAlign('center')
		ctx.setTextBaseline('middle')
		if (sliceAngle > 0.3) {
			ctx.fillText(pct + '%', lx, ly)
		}
		startAngle += sliceAngle
	}

	// 图例
	const legendY = canvasHeight - 6
	let legendX = 10
	for (let i = 0; i < data.length && i < 6; i++) {
		const item = data[i]
		ctx.setFillStyle(colors[i % colors.length])
		ctx.fillRect(legendX, legendY - 10, 12, 12)
		ctx.setFontSize(11)
		ctx.setFillStyle('#333333')
		ctx.setTextAlign('left')
		ctx.setTextBaseline('bottom')
		const label = item.categoryName.length > 4 ? item.categoryName.slice(0, 4) + '..' : item.categoryName
		ctx.fillText(label, legendX + 14, legendY + 2)
		legendX += ctx.measureText(label).width + 28
		if (legendX > canvasWidth - 20) break
	}

	ctx.draw()
}

function drawBarChart() {
	const ctx = uni.createCanvasContext('barCanvas', instance.proxy)
	const canvasWidth = 375
	const canvasHeight = 280
	const chartPadding = 40
	const barChartBottom = 50
	const chartLeft = chartPadding
	const chartRight = canvasWidth - 16
	const chartTop = 20
	const chartBottom = canvasHeight - barChartBottom
	const chartWidth = chartRight - chartLeft
	const chartHeight = chartBottom - chartTop

	ctx.clearRect(0, 0, canvasWidth, canvasHeight)

	const data = monthlyData.value
	if (data.length === 0) {
		ctx.setFontSize(16)
		ctx.setFillStyle('#CCCCCC')
		ctx.setTextAlign('center')
		ctx.fillText('暂无数据', canvasWidth / 2, canvasHeight / 2)
		ctx.draw()
		return
	}

	let maxVal = 0
	for (const item of data) {
		if (Number(item.income) > maxVal) maxVal = Number(item.income)
		if (Number(item.expense) > maxVal) maxVal = Number(item.expense)
	}
	if (maxVal === 0) {
		ctx.setFontSize(16)
		ctx.setFillStyle('#CCCCCC')
		ctx.setTextAlign('center')
		ctx.fillText('暂无数据', canvasWidth / 2, canvasHeight / 2)
		ctx.draw()
		return
	}
	maxVal = Math.ceil(maxVal * 1.2)

	// Y轴网格
	ctx.setStrokeStyle('#EEEEEE')
	ctx.setLineWidth(1)
	for (let i = 0; i <= 4; i++) {
		const y = chartTop + (chartHeight * (4 - i) / 4)
		ctx.beginPath()
		ctx.moveTo(chartLeft, y)
		ctx.lineTo(chartRight, y)
		ctx.stroke()

		ctx.setFontSize(10)
		ctx.setFillStyle('#999999')
		ctx.setTextAlign('right')
		ctx.setTextBaseline('middle')
		ctx.fillText(Math.round(maxVal * i / 4).toString(), chartLeft - 8, y)
	}

	// 柱子
	const dataCount = data.length
	if (dataCount === 0) return
	const groupWidth = chartWidth / dataCount
	const barWidth = groupWidth * 0.25
	const barGap = groupWidth * 0.1

	for (let i = 0; i < dataCount; i++) {
		const item = data[i]
		const incomeH = (Number(item.income) / maxVal) * chartHeight
		const expenseH = (Number(item.expense) / maxVal) * chartHeight
		const x = chartLeft + i * groupWidth + barGap

		if (Number(item.income) > 0) {
			ctx.setFillStyle('#34C759')
			ctx.fillRect(x, chartTop + chartHeight - incomeH, barWidth, incomeH)
		}
		if (Number(item.expense) > 0) {
			ctx.setFillStyle('#FF3B30')
			ctx.fillRect(x + barWidth + barGap, chartTop + chartHeight - expenseH, barWidth, expenseH)
		}

		ctx.setFontSize(10)
		ctx.setFillStyle('#666666')
		ctx.setTextAlign('center')
		ctx.setTextBaseline('top')
		const label = item.period.length >= 7 ? item.period.slice(5) : item.period
		ctx.fillText(label + '月', x + barWidth + barGap, chartBottom + 4)

		ctx.setFontSize(10)
		ctx.setFillStyle('#999999')
		ctx.setTextAlign('center')
		ctx.setTextBaseline('bottom')
		if (Number(item.income) > 0) {
			ctx.fillText('¥' + Math.round(Number(item.income)), x + barWidth / 2, chartTop + chartHeight - incomeH - 4)
		}
		if (Number(item.expense) > 0) {
			ctx.fillText('¥' + Math.round(Number(item.expense)), x + barWidth + barGap + barWidth / 2, chartTop + chartHeight - expenseH - 4)
		}
	}

	// 图例
	ctx.setFontSize(11)
	ctx.setFillStyle('#34C759')
	ctx.fillRect(canvasWidth / 2 - 70, chartBottom + 24, 16, 16)
	ctx.setFillStyle('#666666')
	ctx.setTextAlign('left')
	ctx.setTextBaseline('middle')
	ctx.fillText('收入', canvasWidth / 2 - 50, chartBottom + 32)

	ctx.setFillStyle('#FF3B30')
	ctx.fillRect(canvasWidth / 2 + 10, chartBottom + 24, 16, 16)
	ctx.setFillStyle('#666666')
	ctx.fillText('支出', canvasWidth / 2 + 30, chartBottom + 32)

	ctx.draw()
}
</script>

<style scoped>
.statistics-page {
	display: flex;
	flex-direction: column;
	height: 100%;
	overflow-y: auto;
	min-height: 0;
}
.page-title {
	font-size: 22px;
	font-weight: bold;
	padding: 8px 16px;
}
.view-switch {
	display: flex;
	padding: 0 16px 8px;
}
.switch-btn {
	font-size: 14px;
	color: #666;
	background: #F5F5F5;
	padding: 6px 20px;
	border-radius: 16px;
	margin-right: 10px;
}
.switch-btn.active {
	color: #fff;
	background: #007AFF;
}
.month-nav {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 8px 0;
}
.nav-btn {
	width: 36px;
	height: 36px;
	line-height: 36px;
	text-align: center;
	border-radius: 50%;
	background: #F0F0F0;
	font-size: 18px;
	font-weight: bold;
}
.month-label {
	font-size: 18px;
	font-weight: 500;
	margin: 0 20px;
}
.cat-switch {
	display: flex;
	justify-content: center;
	padding: 8px 0;
}
.cat-btn {
	font-size: 14px;
	color: #666;
	background: #F0F0F0;
	width: 80px;
	height: 32px;
	line-height: 32px;
	text-align: center;
	border-radius: 16px;
	margin: 0 6px;
}
.cat-btn.active {
	color: #fff;
	background: #007AFF;
}
.total-amount {
	font-size: 16px;
	font-weight: 500;
	text-align: center;
	display: block;
	padding: 4px 0;
}
.chart-canvas {
	width: 100%;
	height: 280px;
}
.section-title {
	font-size: 16px;
	font-weight: 500;
	padding: 12px 16px 8px;
	display: block;
}
.detail-list {
	padding: 0 0 20px;
}
.detail-row {
	display: flex;
	align-items: center;
	padding: 10px 16px;
}
.detail-left {
	display: flex;
	align-items: center;
	flex: 1;
}
.color-dot {
	width: 14px;
	height: 14px;
	border-radius: 50%;
	margin-right: 10px;
}
.detail-name {
	font-size: 16px;
	color: #333;
}
.detail-amount {
	font-size: 16px;
	font-weight: 500;
	color: #333;
	margin-right: 12px;
}
.detail-pct {
	font-size: 14px;
	color: #999;
	width: 56px;
	text-align: right;
}
.empty-stat {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40px 0;
	color: #ccc;
	font-size: 15px;
}
</style>
