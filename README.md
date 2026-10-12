# 算了吧记账

一款支持多用户记账、收支统计与 AI 财务问答的记账小程序。前后端全流程独立设计与开发。

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white)
![uni-app](https://img.shields.io/badge/uni--app-3.0-2B9939)
![TypeScript](https://img.shields.io/badge/TypeScript-5.4-3178C6?logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## 功能特性

- **用户模块**：注册、登录、修改密码，基于 JWT 的无状态鉴权
- **记账模块**：收入/支出的增删改查，支持按类型、分类、日期区间、备注多条件分页筛选
- **分类统计**：按分类聚合的饼图数据，直观展示各项支出/收入占比
- **月度趋势**：按月汇总的柱状图数据，展示全年收支走势
- **AI 财务助手**：对接 DeepSeek 大模型，将用户财务数据作为上下文，回答"这个月哪类花得最多""怎么省钱"等问题
- **多端适配**：一套代码同时编译微信小程序与 H5

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5、Java 17 |
| 持久层 | MyBatis-Plus 3.5.7、MySQL 8 |
| 鉴权 | JJWT 0.12.5（JWT 无状态登录） |
| 接口文档 | Knife4j 4.5.0（OpenAPI 3） |
| AI 能力 | DeepSeek Chat API |
| 前端框架 | uni-app 3.0、Vue 3（`<script setup>`）、TypeScript |
| 构建工具 | Vite 5、Sass |

## 项目结构

```
yd/
├── account-houduan/                 # Spring Boot 后端
│   ├── src/main/java/com/harmony/expense/
│   │   ├── controller/              # 接口层
│   │   ├── service/                 # 业务层
│   │   ├── mapper/                  # MyBatis-Plus 数据访问层
│   │   ├── entity/ dto/             # 实体与传输对象
│   │   ├── config/                  # 跨域、MyBatis-Plus、Knife4j 等配置
│   │   ├── interceptor/             # JWT 登录拦截器
│   │   └── common/                  # 统一响应体、全局异常处理
│   └── src/main/resources/
│       ├── application.yml          # 主配置
│       └── schema.sql               # 数据库建表 + 预置数据脚本
├── account2/                        # uni-app 前端
│   └── src/
│       ├── api/                     # 接口封装（含统一 request 层）
│       ├── components/              # 账单 / 统计 / AI / 我的 四大模块
│       ├── pages/                   # 页面
│       └── utils/types.ts           # TypeScript 类型定义
└── harmony_expense.sql              # 完整数据库导出（结构 + 数据）
```

## 快速开始

### 1. 准备数据库

```sql
-- 执行建表脚本（会自动创建库与预置分类/测试数据）
source account-houduan/src/main/resources/schema.sql;
```

### 2. 启动后端

```bash
cd account-houduan
mvn spring-boot:run
```

服务默认运行在 `http://localhost:8081`。

启动前可通过环境变量覆盖敏感配置（均有默认值，本地开发开箱即用）：

| 环境变量 | 说明 | 默认 |
|----------|------|------|
| `DEEPSEEK_API_KEY` | DeepSeek API Key（**AI 功能必填**） | 空 |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `123456` |
| `JWT_SECRET` | JWT 签名密钥 | 内置开发密钥 |

接口文档（Knife4j）：启动后访问 `http://localhost:8081/doc.html`

### 3. 启动前端

```bash
cd account2
npm install

# 微信小程序：用微信开发者工具打开 account2/dist/dev/mp-weixin
npm run dev:mp-weixin

# 或 H5
npm run dev:h5
```

> 前端请求地址在 `account2/src/api/request.ts` 的 `BASE_URL` 中配置，请改成你后端服务的实际地址。

## 接口一览

所有需鉴权接口须在请求头携带 `Authorization: Bearer <token>`。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录，返回 JWT |
| GET | `/api/user/info` | 获取当前用户信息 |
| PUT | `/api/user/password` | 修改密码 |
| POST | `/api/expense` | 添加记账 |
| PUT | `/api/expense/{id}` | 修改记账 |
| DELETE | `/api/expense/{id}` | 删除记账（逻辑删除） |
| GET | `/api/expense/list` | 分页查询记账列表 |
| GET | `/api/expense/{id}` | 获取单条记账详情 |
| GET | `/api/expense/statistics/category` | 分类统计（饼图数据） |
| GET | `/api/expense/statistics/monthly` | 月度趋势（柱状图数据） |
| GET | `/api/category/list` | 获取收支分类列表 |
| POST | `/api/ai/chat` | AI 财务问答 |

## 数据库设计

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `category` | 收支分类表（预置 10 项支出、5 项收入分类） |
| `expense_record` | 记账记录表 |

## 说明

- 密码采用 MD5 + 盐存储。作为个人练习项目，生产环境建议改用 BCrypt 等自适应哈希算法。
- AI 功能依赖 `DEEPSEEK_API_KEY`，未配置时该接口返回友好降级提示，不影响其他功能。
- 仓库内 `schema.sql` 与 `harmony_expense.sql` 中的账号（`test` / `demo`）均为测试数据，密码统一为 `123456`。

## 界面预览

<!-- 截图占位：建议放 2-4 张，如账单页、统计页、AI 对话页 -->

## License

[MIT](https://choosealicense.com/licenses/mit/)
