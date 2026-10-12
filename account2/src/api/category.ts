import { request } from './request'
import { ApiResult, Category } from '@/utils/types'

export function getCategoryList(type?: number): Promise<ApiResult<Category[]>> {
	let path = '/api/category/list'
	if (type !== undefined) {
		path += '?type=' + type
	}
	return request<Category[]>('GET', path)
}
