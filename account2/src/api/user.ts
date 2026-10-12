import { request } from './request'
import { ApiResult, UserInfo } from '@/utils/types'

export function getUserInfo(): Promise<ApiResult<UserInfo>> {
	return request<UserInfo>('GET', '/api/user/info')
}

export function updatePassword(
	oldPassword: string,
	newPassword: string
): Promise<ApiResult<void>> {
	return request<void>('PUT', '/api/user/password', { oldPassword, newPassword })
}
