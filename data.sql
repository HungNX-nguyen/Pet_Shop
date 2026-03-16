DROP DATABASE IF EXISTS petlover;
CREATE DATABASE petlover;
USE petlover;

------------------------------------------------
-- ACCOUNT
------------------------------------------------
CREATE TABLE Accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP 
              ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

------------------------------------------------
-- ROLES
------------------------------------------------
CREATE TABLE Roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB;

INSERT INTO Roles(role_name) VALUES
('SHOP_OWNER'),
('CUSTOMER');

------------------------------------------------
-- ACCOUNT ROLES
------------------------------------------------
CREATE TABLE AccountRoles (
    account_id INT,
    role_id INT,
    PRIMARY KEY (account_id, role_id),
    FOREIGN KEY (account_id) REFERENCES Accounts(account_id),
    FOREIGN KEY (role_id) REFERENCES Roles(role_id)
) ENGINE=InnoDB;

------------------------------------------------
-- PETS
------------------------------------------------
CREATE TABLE Pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    name VARCHAR(100),
    type VARCHAR(100),
    breed VARCHAR(100),
    gender VARCHAR(10),
    weight FLOAT,
    age INT,
    FOREIGN KEY (owner_id) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- CATEGORIES
------------------------------------------------
CREATE TABLE Categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(500)
) ENGINE=InnoDB;

------------------------------------------------
-- PRODUCTS
------------------------------------------------
CREATE TABLE Products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_by INT NOT NULL,
    category_id INT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    stock_quantity INT,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES Accounts(account_id),
    FOREIGN KEY (category_id) REFERENCES Categories(id)
) ENGINE=InnoDB;

------------------------------------------------
-- SERVICES
------------------------------------------------
CREATE TABLE Services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_by INT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    duration INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- CARTS  [MỚI]
------------------------------------------------
CREATE TABLE Carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL UNIQUE,  -- mỗi account chỉ có 1 cart
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
              ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- CART ITEMS  [MỚI]
------------------------------------------------
CREATE TABLE CartItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    UNIQUE KEY uq_cart_product (cart_id, product_id),  -- tránh trùng sản phẩm
    FOREIGN KEY (cart_id) REFERENCES Carts(id),
    FOREIGN KEY (product_id) REFERENCES Products(id)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKINGS
------------------------------------------------
CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    pet_id INT,                        -- [THÊM] booking cho thú cưng nào
    booking_code VARCHAR(50),
    booking_date DATE,
    time_slot VARCHAR(50),
    status VARCHAR(50),
    total_price DECIMAL(10,2),
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Accounts(account_id),
    FOREIGN KEY (pet_id) REFERENCES Pets(pet_id)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKING SERVICES
------------------------------------------------
CREATE TABLE BookingServices (
    booking_id INT,
    service_id INT,
    PRIMARY KEY (booking_id, service_id),
    FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    FOREIGN KEY (service_id) REFERENCES Services(id)
) ENGINE=InnoDB;

------------------------------------------------
-- ORDERS
------------------------------------------------
CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_code VARCHAR(50),
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    shipping_address VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    FOREIGN KEY (customer_id) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- ORDER ITEMS
------------------------------------------------
CREATE TABLE OrderItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT,
    unit_price DECIMAL(10,2),
    sub_total DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES Orders(id),
    FOREIGN KEY (product_id) REFERENCES Products(id)
) ENGINE=InnoDB;

------------------------------------------------
-- PAYMENT HISTORIES
------------------------------------------------
CREATE TABLE PaymentHistories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNIQUE,
    amount DECIMAL(10,2),
    payment_method VARCHAR(100),
    payment_status BOOLEAN,
    payment_date DATETIME,
    FOREIGN KEY (order_id) REFERENCES Orders(id)
) ENGINE=InnoDB;

------------------------------------------------
-- FEEDBACKS  [CẢI THIỆN]
------------------------------------------------
CREATE TABLE Feedbacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,           -- [THÊM] feedback cho sản phẩm nào
    order_id INT NOT NULL,             -- [THÊM] chỉ feedback sau khi đã mua
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_feedback (customer_id, product_id, order_id),  -- mỗi sản phẩm/đơn chỉ feedback 1 lần
    FOREIGN KEY (customer_id) REFERENCES Accounts(account_id),
    FOREIGN KEY (product_id) REFERENCES Products(id),
    FOREIGN KEY (order_id) REFERENCES Orders(id)
) ENGINE=InnoDB;