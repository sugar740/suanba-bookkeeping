export interface ApiResult<T> {
	code: number;
	message: string;
	data: T;
}

export interface LoginResponse {
	token: string;
	nickname: string;
	avatar: string;
}

export interface UserInfo {
	id: number;
	username: string;
	nickname: string;
	email: string;
	avatar: string;
}

export interface Category {
	id: number;
	name: string;
	type: number; // 0=支出 1=收入
}

export interface Expense {
	id: number;
	userId: number;
	type: number;
	amount: number;
	categoryId: number;
	expenseDate: string;
	remark: string;
	deleted: number;
	createTime: string;
	updateTime: string;
}

export interface ExpenseDTO {
	type: number;
	amount: number;
	categoryId: number;
	expenseDate?: string;
	remark?: string;
}

export interface ExpenseQuery {
	type?: number;
	categoryId?: number;
	startDate?: string;
	endDate?: string;
	remark?: string;
	page: number;
	size: number;
}

export interface PageResult<T> {
	total: number;
	page: number;
	size: number;
	records: T[];
}

export interface CategoryStatistics {
	categoryId: number;
	categoryName: string;
	totalAmount: number;
	count: number;
}

export interface MonthlyStatistics {
	period: string;
	income: number;
	expense: number;
}

export interface ChatRequest {
	question: string;
}

export interface ChatResponse {
	answer: string;
}
