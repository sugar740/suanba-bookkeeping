<template>
	<view class="index-page">
		<!-- 自定义标题栏 -->
		<view class="title-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
			<view class="title-bar-inner">
				<text class="app-title">算了吧记账</text>
			</view>
		</view>

		<!-- Tab 内容区域 -->
		<swiper
			class="tab-content"
			:current="currentTab"
			@change="onSwiperChange"
			:duration="200"
		>
			<swiper-item>
				<bill-list ref="billListRef" :refresh-key="billRefreshKey" />
			</swiper-item>
			<swiper-item>
				<statistics ref="statisticsRef" />
			</swiper-item>
			<swiper-item>
				<ai-chat />
			</swiper-item>
			<swiper-item>
				<profile />
			</swiper-item>
		</swiper>

		<!-- 底部 Tab 栏 -->
		<view class="tab-bar">
			<view
				v-for="(tab, idx) in tabs"
				:key="idx"
				class="tab-item"
				@click="switchTab(idx)"
			>
				<text class="tab-icon">{{ tab.icon }}</text>
				<text class="tab-label" :class="{ active: currentTab === idx }">{{ tab.label }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getToken } from '@/api/request'
import BillList from '@/components/bill-list.vue'
import Statistics from '@/components/statistics.vue'
import AiChat from '@/components/ai-chat.vue'
import Profile from '@/components/profile.vue'

const tabs = [
	{ label: '账单', icon: '💰' },
	{ label: '统计', icon: '📊' },
	{ label: 'AI', icon: '🤖' },
	{ label: '我的', icon: '👤' },
]

const currentTab = ref(0)
const statusBarHeight = ref(20)
const billRefreshKey = ref(0)
const billListRef = ref(null)
const statisticsRef = ref(null)

onMounted(() => {
	try {
		const sys = uni.getSystemInfoSync()
		statusBarHeight.value = sys.statusBarHeight || 20
	} catch {}

	// 检查登录状态
	const token = getToken()
	if (!token) {
		uni.reLaunch({ url: '/pages/login/login' })
	}
})

onShow(() => {
	// 每次回到 tab 时刷新账单列表
	if (currentTab.value === 0) {
		billRefreshKey.value++
	}
	// 回到统计 tab 时刷新
	if (currentTab.value === 1 && statisticsRef.value) {
		statisticsRef.value.fetchData()
	}
})

function switchTab(idx) {
	if (currentTab.value === idx) return
	currentTab.value = idx
	// 切换到账单 tab 时刷新
	if (idx === 0) {
		billRefreshKey.value++
		setTimeout(() => {
			if (billListRef.value) {
				billListRef.value.refresh()
			}
		}, 100)
	}
	if (idx === 1 && statisticsRef.value) {
		statisticsRef.value.fetchData()
	}
}

function onSwiperChange(e) {
	currentTab.value = e.detail.current
	if (e.detail.current === 0) {
		billRefreshKey.value++
		setTimeout(() => {
			if (billListRef.value) billListRef.value.refresh()
		}, 100)
	}
	if (e.detail.current === 1 && statisticsRef.value) {
		statisticsRef.value.fetchData()
	}
}
</script>

<style scoped>
.index-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background: #fff;
}
.title-bar {
	background: #fff;
	border-bottom: 1rpx solid #F0F0F0;
}
.title-bar-inner {
	height: 44px;
	display: flex;
	align-items: center;
	padding: 0 16px;
}
.app-title {
	font-size: 20px;
	font-weight: bold;
}
.tab-content {
	flex: 1;
	overflow: hidden;
}
.tab-bar {
	display: flex;
	height: 50px;
	border-top: 1rpx solid #F0F0F0;
	background: #fff;
	padding-bottom: env(safe-area-inset-bottom);
}
.tab-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}
.tab-icon {
	font-size: 20px;
	line-height: 1;
}
.tab-label {
	font-size: 10px;
	color: #999;
	margin-top: 2px;
}
.tab-label.active {
	color: #007AFF;
}
</style>
