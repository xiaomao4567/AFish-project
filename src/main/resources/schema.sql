CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    open_id VARCHAR(255) NOT NULL UNIQUE COMMENT '微信OpenID',
    nickname VARCHAR(100) DEFAULT '微信用户' COMMENT '用户昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    table_number INT COMMENT '桌号',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '订单金额',
    status VARCHAR(20) NOT NULL DEFAULT 'PAID' COMMENT '订单状态：PAID-已支付，PREPARING-备餐中，SERVED-已出餐，CANCELLED-已取消',
    remark VARCHAR(500) COMMENT '备注',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    user_id BIGINT COMMENT '下单用户ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    prepare_time DATETIME COMMENT '备餐开始时间',
    finish_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_table_number (table_number),
    INDEX idx_status (status),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';