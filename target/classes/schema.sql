CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    category_Id INT NOT NULL,
    weight DECIMAL(5, 2),
    current_stock INT NOT NULL,
    min_stock INT NOT NULL
);