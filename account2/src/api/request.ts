import { ApiResult } from '@/utils/types'

const BASE_URL = 'http://10.30.53.106:8081'

export function getToken(): string {
	try {
		return uni.getStorageSync('token') || ''
	} catch {
		return ''
	}
}

export function saveToken(token: string): void {
	uni.setStorageSync('token', token)
}

export function clearToken(): void {
	uni.removeStorageSync('token')
}

export async function request<T>(
	method: 'GET' | 'POST' | 'PUT' | 'DELETE',
	path: string,
	data?: Record<string, any>
): Promise<ApiResult<T>> {
	const token = getToken()
	const header: Record<string, string> = {
		'Content-Type': 'application/json',
	}
	if (token) {
		header['Authorization'] = 'Bearer ' + token
	}

	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + path,
			method,
			header,
			data,
			timeout: 10000,
			success: (res) => {
				const statusCode = res.statusCode
				if (statusCode >= 200 && statusCode < 300) {
					resolve(res.data as ApiResult<T>)
				} else {
					const apiResult = res.data as ApiResult<T>
					reject(new Error(apiResult?.message || '请求失败'))
				}
			},
			fail: (err) => {
				reject(new Error(err.errMsg || '网络错误'))
			},
		})
	})
}
