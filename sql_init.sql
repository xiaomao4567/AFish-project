CREATE DATABASE IF NOT EXISTS afish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE afish;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    status TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS combo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    original_price DECIMAL(10,2),
    discount_price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS combo_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    combo_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (combo_id) REFERENCES combo(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'EMPLOYEE',
    phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_number INT NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    flavor VARCHAR(100),
    quantity INT DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
);

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    table_number INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT '待支付',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    pay_time DATETIME
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    flavor VARCHAR(100),
    quantity INT DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
);

INSERT INTO employee (username, password, real_name, role, phone) VALUES 
('admin', 'admin123', '管理员', 'ADMIN', '13800138000');

INSERT INTO category (name, sort_order) VALUES 
('热门菜品', 1),
('招牌菜', 2),
('凉菜', 3),
('热菜', 4),
('汤品', 5),
('主食', 6);

INSERT INTO dish (name, category_id, price, description, sort_order) VALUES 
('红烧肉', 2, 68.00, '精选五花肉，慢火炖制', 1),
('清蒸鲈鱼', 2, 88.00, '新鲜鲈鱼，清蒸入味', 2),
('宫保鸡丁', 4, 48.00, '经典川菜，麻辣鲜香', 1),
('麻婆豆腐', 4, 28.00, '正宗川味，麻辣嫩爽', 2),
('凉拌黄瓜', 3, 18.00, '清爽可口，开胃小菜', 1),
('酸辣土豆丝', 3, 22.00, '酸辣适中，下饭神器', 2),
('西红柿鸡蛋汤', 5, 20.00, '家常味道，营养美味', 1),
('米饭', 6, 3.00, '五常大米，粒粒饱满', 1);
