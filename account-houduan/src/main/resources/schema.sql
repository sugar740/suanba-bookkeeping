CREATE DATABASE IF NOT EXISTS harmony_expense DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE harmony_expense;


-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(200) NOT NULL,
    nickname    VARCHAR(50)  NULL,
    email       VARCHAR(100) NULL,
    avatar      VARCHAR(500) NULL,
    deleted     TINYINT(1)   DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type TINYINT(1)  NOT NULL COMMENT '0=支出 1=收入'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '分类表';

-- 记账表
CREATE TABLE IF NOT EXISTS expense_record (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT        NOT NULL,
    type         TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '0=支出 1=收入',
    amount       DECIMAL(10,2) NOT NULL,
    category_id  BIGINT        NOT NULL,
    expense_date DATE          DEFAULT (CURRENT_DATE),
    remark       VARCHAR(500)  NULL,
    deleted      TINYINT(1)    DEFAULT 0,
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_expense_date (expense_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '记账记录表';

-- ======== 预置分类 ========
INSERT IGNORE INTO category (id, name, type) VALUES
-- 支出
(1, '餐饮', 0),
(2, '交通', 0),
(3, '购物', 0),
(4, '娱乐', 0),
(5, '住房', 0),
(6, '通讯', 0),
(7, '医疗', 0),
(8, '教育', 0),
(9, '人情', 0),
(10, '其他支出', 0),
-- 收入
(11, '工资', 1),
(12, '兼职', 1),
(13, '红包', 1),
(14, '投资收益', 1),
(15, '其他收入', 1);

-- ======== 测试数据 ========

-- 测试用户（密码都是 123456）
INSERT IGNORE INTO sys_user (id, username, password, nickname, email) VALUES
(1, 'test',  '3e979f4a9d0df06039df465915a4298e', '测试用户', 'test@example.com'),
(2, 'demo',  '3e979f4a9d0df06039df465915a4298e', '演示用户', 'demo@example.com');
-- UPDATE sys_user SET password = 'ca089c6df8f709501019f267474b7a1f' WHERE username = 'test';密码设置为123456
-- 测试记账记录（用户1，2026年4月-5月）
INSERT IGNORE INTO expense_record (id, user_id, type, amount, category_id, expense_date, remark) VALUES
-- 5月支出
(1,  1, 0, 35.00,  1,  '2026-05-01', '早餐'),
(2,  1, 0, 68.00,  1,  '2026-05-02', '午餐'),
(3,  1, 0, 120.00, 2,  '2026-05-03', '打车'),
(4,  1, 0, 299.00, 3,  '2026-05-05', '日用品'),
(5,  1, 0, 45.00,  1,  '2026-05-06', '奶茶'),
(6,  1, 0, 15.00,  2,  '2026-05-07', '公交'),
(7,  1, 0, 188.00, 4,  '2026-05-08', '电影票'),
(8,  1, 0, 56.00,  1,  '2026-05-10', '外卖'),
(9,  1, 0, 800.00, 5,  '2026-05-12', '水电燃气'),
(10, 1, 0, 30.00,  6,  '2026-05-15', '话费充值'),
(11, 1, 1, 15000,  11, '2026-05-15', '5月工资'),
(12, 1, 0, 88.00,  1,  '2026-05-18', '晚餐'),
-- 4月支出

(13, 1, 0, 350.00, 3,  '2026-04-03', '春装'),
(14, 1, 0, 200.00, 4,  '2026-04-05', 'KTV'),
(15, 1, 0, 50.00,  1,  '2026-04-08', '早餐'),
(16, 1, 0, 75.00,  2,  '2026-04-10', '打车'),
(17, 1, 1, 15000,  11, '2026-04-15', '4月工资'),
(18, 1, 1, 500.00, 12, '2026-04-20', '周末兼职');