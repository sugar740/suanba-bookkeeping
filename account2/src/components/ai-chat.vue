<template>
	<view class="ai-chat">
		<text class="page-title">AI 对话</text>

		<scroll-view scroll-y class="msg-scroll" :scroll-into-view="scrollId" scroll-with-animation>
			<view class="msg-list">
				<view
					v-for="(msg, idx) in messages"
					:key="idx"
					:id="'msg-' + idx"
					class="msg-row"
					:class="{ 'msg-user': msg.role === 'user', 'msg-ai': msg.role === 'ai' }"
				>
					<view class="bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
						<text>{{ msg.content }}</text>
					</view>
				</view>

				<view v-if="loading" id="msg-loading" class="msg-row msg-ai">
					<view class="bubble bubble-ai thinking">
						<text>AI 正在思考...</text>
					</view>
				</view>

				<view style="height:20px"></view>
			</view>
		</scroll-view>

		<view class="input-bar">
			<input
				v-model="inputText"
				class="msg-input"
				placeholder="输入你的财务问题..."
				:disabled="loading"
				@confirm="sendMessage"
			/>
			<button class="send-btn" :disabled="loading || !inputText.trim()" @click="sendMessage">发送</button>
		</view>
	</view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChat } from '@/api/ai'

const messages = ref([
	{
		role: 'ai',
		content: '你好！我是你的财务助手。\n\n你可以问我：\n• "我这个月花了多少钱？"\n• "我的钱主要花在哪了？"\n• "有什么省钱建议吗？"'
	}
])
const inputText = ref('')
const loading = ref(false)
const scrollId = ref('')

function scrollToBottom() {
	nextTick(() => {
		scrollId.value = 'msg-' + (messages.value.length - 1)
	})
}

async function sendMessage() {
	const text = inputText.value.trim()
	if (!text || loading.value) return

	inputText.value = ''
	messages.value.push({ role: 'user', content: text })
	scrollToBottom()

	loading.value = true
	try {
		const res = await aiChat(text)
		if (res.code === 200 && res.data) {
			messages.value.push({ role: 'ai', content: res.data.answer })
		} else {
			messages.value.push({ role: 'ai', content: '抱歉：' + (res.message || '服务异常') })
		}
	} catch {
		messages.value.push({ role: 'ai', content: 'AI 服务暂时不可用，请稍后再试。' })
	} finally {
		loading.value = false
		scrollToBottom()
	}
}
</script>

<style scoped>
.ai-chat {
	display: flex;
	flex-direction: column;
	height: 100%;
	overflow: hidden;
}
.page-title {
	font-size: 22px;
	font-weight: bold;
	padding: 8px 16px;
}
.msg-scroll {
	flex: 1;
	min-height: 0;
	background: #FAFAFA;
}
.msg-list {
	padding: 12px 0;
}
.msg-row {
	display: flex;
	padding: 6px 16px;
}
.msg-user {
	justify-content: flex-end;
}
.msg-ai {
	justify-content: flex-start;
}
.bubble {
	max-width: 70%;
	padding: 10px 14px;
	border-radius: 16px;
	font-size: 15px;
	line-height: 1.5;
	word-break: break-all;
}
.bubble-user {
	background: #007AFF;
	color: #fff;
	border-bottom-right-radius: 4px;
}
.bubble-ai {
	background: #F0F0F0;
	color: #333;
	border-bottom-left-radius: 4px;
}
.thinking {
	color: #999;
}
.input-bar {
	display: flex;
	align-items: center;
	padding: 8px 12px 16px;
	background: #fff;
	border-top-left-radius: 16px;
	border-top-right-radius: 16px;
	box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
}
.msg-input {
	flex: 1;
	height: 44px;
	background: #fff;
	border: 1px solid #E0E0E0;
	border-radius: 22px;
	padding: 0 16px;
	font-size: 15px;
}
.send-btn {
	height: 44px;
	line-height: 44px;
	padding: 0 18px;
	margin-left: 8px;
	background: #007AFF;
	color: #fff;
	border-radius: 22px;
	font-size: 15px;
	border: none;
}
.send-btn[disabled] {
	opacity: 0.5;
}
</style>
