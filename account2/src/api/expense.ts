import { request } from './request'
import {
	ApiResult, Expense, ExpenseDTO, ExpenseQuery,
	PageResult, CategoryStatistics, MonthlyStatistics
} from '@/utils/types'

export function getExpenseList(params: ExpenseQuery): Promise<ApiResult<PageResult<Expense>>> {
	let path = '/api/expense/list?page=' + params.page + '&size=' + params.size
	if (params.type !== undefined) path += '&type=' + params.type
	if (params.categoryId !== undefined) path += '&categoryId=' + params.categoryId
	if (params.startDate !== undefined) path += '&startDate=' + params.startDate
	if (params.endDate !== undefined) path += '&endDate=' + params.endDate
	if (params.remark) path += '&remark=' + encodeURIComponent(params.remark)
	return request<PageResult<Expense>>('GET', path)
}

export function addExpense(dto: ExpenseDTO): Promise<ApiResult<void>> {
	return request<void>('POST', '/api/expense', dto as any)
}

export function updateExpense(id: number, dto: ExpenseDTO): Promise<ApiResult<void>> {
	return request<void>('PUT', '/api/expense/' + id, dto as any)
}

export function deleteExpense(id: number): Promise<ApiResult<void>> {
	return request<void>('DELETE', '/api/expense/' + id)
}

export function getExpenseDetail(id: number): Promise<ApiResult<Expense>> {
	return request<Expense>('GET', '/api/expense/' + id)
}

export function categoryStatistics(
	type: number,
	startDate: string,
	endDate: string
): Promise<ApiResult<CategoryStatistics[]>> {
	const path = '/api/expense/statistics/category?type=' + type +
		'&startDate=' + startDate + '&endDate=' + endDate
	return request<CategoryStatistics[]>('GET', path)
}

export function monthlyStatistics(
	startDate: string,
	endDate: string
): Promise<ApiResult<MonthlyStatistics[]>> {
	const path = '/api/expense/statistics/monthly?startDate=' + startDate +
		'&endDate=' + endDate
	return request<MonthlyStatistics[]>('GET', path)
}
