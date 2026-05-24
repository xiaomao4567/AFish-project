CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    open_id VARCHAR(255) NOT NULL UNIQUE COMMENT '微信OpenID',
    nickname VARCHAR(100) DEFAULT '微信用户' COMMENT '用户昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';