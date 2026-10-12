<template>
	<view class="login-page">
		<view class="header">
			<view class="logo">
				<text class="logo-icon">💰</text>
			</view>
			<text class="app-name">算了吧记账</text>
			<text class="slogan">记录每一笔，掌控你的财务</text>
		</view>

		<view class="form-card">
			<view class="field">
				<text class="field-label">用户名</text>
				<input
					v-model="username"
					class="field-input"
					placeholder="请输入用户名"
					:maxlength="20"
				/>
			</view>
			<view class="field">
				<text class="field-label">密码</text>
				<input
					v-model="password"
					class="field-input"
					placeholder="请输入密码"
					:maxlength="32"
					type="password"
				/>
			</view>

			<button class="btn-login" :disabled="loading" @click="handleLogin">
				{{ loading ? '登录中...' : '登 录' }}
			</button>
		</view>

		<text class="link" @click="goRegister">还没有账号？去注册</text>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '@/api/auth'
import { saveToken } from '@/api/request'

const username = ref('')
const password = ref('')
const loading = ref(false)

async function handleLogin() {
	if (!username.value.trim()) {
		uni.showToast({ title: '请输入用户名', icon: 'none' })
		return
	}
	if (!password.value.trim()) {
		uni.showToast({ title: '请输入密码', icon: 'none' })
		return
	}

	loading.value = true
	try {
		const res = await login(username.value.trim(), password.value.trim())
		if (res.code === 200) {
			saveToken(res.data.token)
			uni.showToast({ title: '登录成功', icon: 'success' })
			uni.reLaunch({ url: '/pages/index/index' })
		} else {
			uni.showToast({ title: res.message || '登录失败', icon: 'none' })
		}
	} catch (e) {
		uni.showToast({ title: e.message || '登录失败', icon: 'none' })
	} finally {
		loading.value = false
	}
}

function goRegister() {
	uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<style scoped>
.login-page {
	min-height: 100vh;
	background: #F5F6FA;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 0 32px;
}
.header {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 80px;
	padding-bottom: 36px;
}
.logo {
	width: 72px;
	height: 72px;
	border-radius: 20px;
	background: linear-gradient(135deg, #007AFF, #00C7BE);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 16px;
	box-shadow: 0 6px 20px rgba(0, 122, 255, 0.25);
}
.logo-icon {
	font-size: 34px;
}
.app-name {
	font-size: 26px;
	font-weight: bold;
	color: #1a1a1a;
	margin-bottom: 6px;
}
.slogan {
	font-size: 14px;
	color: #999;
}
.form-card {
	width: 100%;
	background: #fff;
	border-radius: 16px;
	padding: 28px 24px 24px;
	box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
}
.field {
	margin-bottom: 20px;
}
.field-label {
	font-size: 14px;
	font-weight: 500;
	color: #444;
	display: block;
	margin-bottom: 8px;
}
.field-input {
	height: 48px;
	background: #F7F8FA;
	border-radius: 10px;
	padding: 0 16px;
	font-size: 16px;
	color: #333;
	width: 100%;
	box-sizing: border-box;
	border: none;
	outline: none;
}
.field-input:focus {
	background: #EEF2FF;
}
.field-input::placeholder {
	color: #c8c8c8;
}
.btn-login {
	width: 100%;
	height: 48px;
	line-height: 48px;
	background: #007AFF;
	color: #fff;
	font-size: 18px;
	font-weight: 500;
	border-radius: 10px;
	margin-top: 8px;
	border: none;
	letter-spacing: 6px;
}
.btn-login[disabled] {
	opacity: 0.5;
}
.link {
	font-size: 14px;
	color: #007AFF;
	text-align: center;
	display: block;
	margin-top: 28px;
}
</style>
