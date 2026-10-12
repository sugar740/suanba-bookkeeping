import { request } from './request'
import { ApiResult, LoginResponse } from '@/utils/types'

export function login(username: string, password: string): Promise<ApiResult<LoginResponse>> {
	return request<LoginResponse>('POST', '/api/user/login', { username, password })
}

export function register(
	username: string,
	password: string,
	nickname?: string
): Promise<ApiResult<void>> {
	const body: Record<string, string> = { username, password }
	if (nickname) {
		body.nickname = nickname
	}
	return request<void>('POST', '/api/user/register', body)
}
