import { request } from './request'
import { ChatResponse, ApiResult } from '@/utils/types'

export function aiChat(question: string): Promise<ApiResult<ChatResponse>> {
	return request<ChatResponse>('POST', '/api/ai/chat', { question })
}
