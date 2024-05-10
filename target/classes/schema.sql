CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    stock BIGINT NOT NULL,
    category VARCHAR(255) NOT NULL,
    discount DECIMAL(4, 2),
    weight DECIMAL(5, 2)
);