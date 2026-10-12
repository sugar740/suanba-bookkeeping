<template>
	<view class="password-page">
		<view class="nav-bar" :style="{ paddingTop: (statusBarHeight + 12) + 'px' }">
			<text class="nav-back" @click="goBack">返回</text>
			<text class="nav-title">修改密码</text>
			<text class="nav-placeholder"></text>
		</view>

		<view class="header">
			<view class="logo">
				<text class="logo-icon">🔐</text>
			</view>
			<text class="header-title">安全设置</text>
			<text class="header-desc">请先验证旧密码，再设置新密码</text>
		</view>

		<view class="form-card">
			<view class="field">
				<text class="field-label">旧密码</text>
				<input
					v-model="oldPassword"
					class="field-input"
					placeholder="请输入旧密码"
					type="password"
				/>
			</view>
			<view class="field">
				<text class="field-label">新密码</text>
				<input
					v-model="newPassword"
					class="field-input"
					placeholder="至少6位，建议包含字母和数字"
					type="password"
				/>
			</view>
			<view class="field">
				<text class="field-label">确认新密码</text>
				<input
					v-model="confirmPassword"
					class="field-input"
					placeholder="请再次输入新密码"
					type="password"
				/>
			</view>

			<button class="btn-submit" :disabled="loading" @click="handleUpdate">
				{{ loading ? '提交中...' : '确认修改' }}
			</button>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { updatePassword } from '@/api/user'

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const loading = ref(false)

async function handleUpdate() {
	if (!oldPassword.value.trim()) {
		uni.showToast({ title: '请输入旧密码', icon: 'none' })
		return
	}
	if (!newPassword.value.trim()) {
		uni.showToast({ title: '请输入新密码', icon: 'none' })
		return
	}
	if (newPassword.value.trim().length < 6) {
		uni.showToast({ title: '新密码至少6位', icon: 'none' })
		return
	}
	if (newPassword.value !== confirmPassword.value) {
		uni.showToast({ title: '两次密码不一致', icon: 'none' })
		return
	}

	loading.value = true
	try {
		const res = await updatePassword(oldPassword.value.trim(), newPassword.value.trim())
		if (res.code === 200) {
			uni.showToast({ title: '密码修改成功', icon: 'success' })
			uni.navigateBack()
		} else {
			uni.showToast({ title: res.message, icon: 'none' })
		}
	} catch (e) {
		uni.showToast({ title: e.message || '修改失败', icon: 'none' })
	} finally {
		loading.value = false
	}
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
</script>

<style scoped>
.password-page {
	min-height: 100vh;
	background: #F5F6FA;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 0 32px;
}
.nav-bar {
	display: flex;
	align-items: center;
	padding: 12px 0;
	width: 100%;
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
.header {
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 100%;
	padding: 12px 0 28px;
}
.logo {
	width: 64px;
	height: 64px;
	border-radius: 18px;
	background: linear-gradient(135deg, #007AFF, #00C7BE);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 14px;
	box-shadow: 0 6px 20px rgba(0, 122, 255, 0.25);
}
.logo-icon {
	font-size: 30px;
}
.header-title {
	font-size: 22px;
	font-weight: bold;
	color: #1a1a1a;
	margin-bottom: 4px;
}
.header-desc {
	font-size: 13px;
	color: #999;
}
.form-card {
	width: 100%;
	background: #fff;
	border-radius: 16px;
	padding: 24px;
	box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
}
.field {
	margin-bottom: 18px;
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
	font-size: 15px;
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
	font-size: 14px;
}
.btn-submit {
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
	letter-spacing: 2px;
}
.btn-submit[disabled] {
	opacity: 0.5;
}
</style>
