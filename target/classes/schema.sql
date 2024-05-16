CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    category_id INT NOT NULL,
    weight DECIMAL(5, 2) NOT NULL,
    current_stock INT NOT NULL,
    min_stock INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);


CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    discount DECIMAL(4, 2) NOT NULL,
    promotion_type VARCHAR(50) NOT NULL,
    volume_threshold INT NOT NULL,
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);