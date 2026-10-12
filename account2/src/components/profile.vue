<template>
	<view class="profile">
		<text class="page-title">我的</text>

		<view class="avatar-section">
			<view class="avatar">
				<text class="avatar-text">U</text>
			</view>
			<text class="nickname">用户</text>
		</view>

		<view class="menu-list">
			<view class="menu-item" @click="showInfo">
				<text>👤  个人信息</text>
				<text class="arrow">›</text>
			</view>
			<view class="divider"></view>
			<view class="menu-item" @click="goUpdatePassword">
				<text>🔒  修改密码</text>
				<text class="arrow">›</text>
			</view>
			<view class="divider"></view>
			<view class="menu-item" @click="showAbout">
				<text>ℹ️  关于</text>
				<text class="arrow">›</text>
			</view>
		</view>

		<view class="logout-area">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</view>
	</view>
</template>

<script setup>
import { clearToken } from '@/api/request'

function showInfo() {
	uni.showToast({ title: '个人信息', icon: 'none' })
}

function showAbout() {
	uni.showToast({ title: '算了吧记账 v1.0.0', icon: 'none' })
}

function goUpdatePassword() {
	uni.navigateTo({ url: '/pages/update-password/password' })
}

async function handleLogout() {
	try {
		await clearToken()
	} catch {}
	uni.showToast({ title: '已退出', icon: 'success' })
	uni.reLaunch({ url: '/pages/login/login' })
}
</script>

<style scoped>
.profile {
	display: flex;
	flex-direction: column;
	height: 100%;
}
.page-title {
	font-size: 22px;
	font-weight: bold;
	padding: 8px 16px;
}
.avatar-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 16px 0 24px;
}
.avatar {
	width: 64px;
	height: 64px;
	border-radius: 50%;
	background: #E0E0E0;
	display: flex;
	align-items: center;
	justify-content: center;
}
.avatar-text {
	font-size: 28px;
	color: #999;
	font-weight: bold;
}
.nickname {
	font-size: 18px;
	font-weight: 500;
	color: #333;
	margin-top: 8px;
}
.menu-list {
	width: 92%;
	margin: 0 auto;
	background: #FAFAFA;
	border-radius: 10px;
}
.menu-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px;
	font-size: 16px;
	color: #333;
}
.arrow {
	font-size: 16px;
	color: #ccc;
}
.divider {
	width: 92%;
	height: 1px;
	background: #F0F0F0;
	margin: 0 auto;
}
.logout-area {
	flex: 1;
	display: flex;
	align-items: flex-end;
	justify-content: center;
	padding-bottom: 40px;
}
.logout-btn {
	width: 85%;
	height: 48px;
	line-height: 48px;
	background: #FF3B30;
	color: #fff;
	border-radius: 8px;
	font-size: 16px;
	border: none;
}
</style>
