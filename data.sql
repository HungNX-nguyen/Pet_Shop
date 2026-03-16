DROP DATABASE IF EXISTS petlover;
CREATE DATABASE petlover;
USE petlover;

------------------------------------------------
-- ACCOUNTS
------------------------------------------------
CREATE TABLE Accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

------------------------------------------------
-- ROLES
------------------------------------------------
CREATE TABLE Roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB;

INSERT INTO Roles (role_name) VALUES
('SHOP_OWNER'),
('CUSTOMER');

------------------------------------------------
-- ACCOUNT ROLES
------------------------------------------------
CREATE TABLE AccountRoles (
    account_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_accountroles_account
        FOREIGN KEY (account_id) REFERENCES Accounts(account_id),
    CONSTRAINT fk_accountroles_role
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
    CONSTRAINT fk_pets_owner
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
-- Khớp với entity Product:
-- @Table(name = "products")
-- created_by, category_id, created_at, updated_at
------------------------------------------------
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by INT,
    category_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_created_by
        FOREIGN KEY (created_by) REFERENCES Accounts(account_id),
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES Categories(id)
) ENGINE=InnoDB;

------------------------------------------------
-- SERVICES
-- Khớp với entity Service:
-- @Table(name = "Services")
-- có category, is_active, created_at, updated_at
------------------------------------------------
CREATE TABLE Services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_by INT NOT NULL,
    name VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    duration INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_services_created_by
        FOREIGN KEY (created_by) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKINGS
------------------------------------------------
CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    booking_code VARCHAR(50),
    booking_date DATE,
    time_slot VARCHAR(50),
    status VARCHAR(50),
    total_price DECIMAL(10,2),
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_customer
        FOREIGN KEY (customer_id) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKING SERVICES
------------------------------------------------
CREATE TABLE BookingServices (
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (booking_id, service_id),
    CONSTRAINT fk_bookingservices_booking
        FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    CONSTRAINT fk_bookingservices_service
        FOREIGN KEY (service_id) REFERENCES Services(id)
) ENGINE=InnoDB;

------------------------------------------------
-- ORDERS
-- Khớp với entity Order:
-- @Table(name = "Orders")
-- @JoinColumn(name = "customer_id")
------------------------------------------------
CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_code VARCHAR(50),
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer
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
    CONSTRAINT fk_orderitems_order
        FOREIGN KEY (order_id) REFERENCES Orders(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orderitems_product
        FOREIGN KEY (product_id) REFERENCES products(id)
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
    CONSTRAINT fk_paymenthistories_order
        FOREIGN KEY (order_id) REFERENCES Orders(id)
) ENGINE=InnoDB;

------------------------------------------------
-- FEEDBACKS
------------------------------------------------
CREATE TABLE Feedbacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    rating INT,
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedbacks_customer
        FOREIGN KEY (customer_id) REFERENCES Accounts(account_id)
) ENGINE=InnoDB;

------------------------------------------------
-- SEED DATA
------------------------------------------------

-- Accounts
INSERT INTO Accounts (
    username, full_name, email, password, phone_number, is_active
) VALUES
('owner01', 'Shop Owner', 'owner01@mypetlove.com', '$2a$10$ownerEncodedPassword', '0901000001', TRUE),
('customer01', 'Nguyen Van A', 'customer01@gmail.com', '$2a$10$customerEncodedPassword1', '0902000001', TRUE),
('customer02', 'Tran Thi B', 'customer02@gmail.com', '$2a$10$customerEncodedPassword2', '0902000002', TRUE),
('customer03', 'Le Van C', 'customer03@gmail.com', '$2a$10$customerEncodedPassword3', '0902000003', TRUE);

-- AccountRoles
INSERT INTO AccountRoles (account_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 2);

-- Pets
INSERT INTO Pets (owner_id, name, type, breed, gender, weight, age) VALUES
(2, 'Milo', 'Dog', 'Poodle', 'Male', 4.5, 3),
(3, 'Luna', 'Cat', 'British Shorthair', 'Female', 3.8, 2),
(4, 'Coco', 'Dog', 'Pomeranian', 'Female', 2.9, 1);

-- Categories
INSERT INTO Categories (name, description) VALUES
('Dog Food', 'Food products for dogs'),
('Cat Food', 'Food products for cats'),
('Accessories', 'Pet accessories and toys'),
('Health Care', 'Health care products for pets');

-- Products
INSERT INTO products (
    name, description, price, stock_quantity, image_url, is_active,
    created_by, category_id, created_at, updated_at
) VALUES
('Royal Canin Mini Adult', 'Premium dry food for small adult dogs', 250000.00, 50, '/uploads/products/royal-canin-mini-adult.jpg', TRUE, 1, 1, NOW(), NOW()),
('Pedigree Puppy Chicken', 'Nutritious puppy food with chicken flavor', 180000.00, 35, '/uploads/products/pedigree-puppy-chicken.jpg', TRUE, 1, 1, NOW(), NOW()),
('Me-O Tuna Adult', 'Tuna flavor food for adult cats', 95000.00, 60, '/uploads/products/meo-tuna-adult.jpg', TRUE, 1, 2, NOW(), NOW()),
('Pet Collar Size M', 'Comfortable collar for medium pets', 70000.00, 40, '/uploads/products/pet-collar-m.jpg', TRUE, 1, 3, NOW(), NOW()),
('Rubber Bone Toy', 'Safe chewing toy for dogs', 45000.00, 80, '/uploads/products/rubber-bone-toy.jpg', TRUE, 1, 3, NOW(), NOW()),
('Pet Vitamin Syrup', 'Vitamin supplement for dogs and cats', 120000.00, 25, '/uploads/products/pet-vitamin-syrup.jpg', TRUE, 1, 4, NOW(), NOW());

-- Services
INSERT INTO Services (
    created_by, name, category, description, price, duration, is_active, created_at, updated_at
) VALUES
(1, 'Basic Grooming', 'GROOMING', 'Bath, drying and brushing', 150000.00, 60, TRUE, NOW(), NOW()),
(1, 'Full Grooming', 'GROOMING', 'Bath, haircut, nail trimming', 250000.00, 90, TRUE, NOW(), NOW()),
(1, 'Health Check', 'HEALTH', 'Basic health examination for pets', 200000.00, 45, TRUE, NOW(), NOW());

-- Bookings
INSERT INTO Bookings (
    customer_id, booking_code, booking_date, time_slot, status, total_price, note, created_at
) VALUES
(2, 'BK-001', '2026-03-18', '09:00 - 10:00', 'PENDING', 150000.00, 'Grooming for Milo', NOW()),
(3, 'BK-002', '2026-03-19', '14:00 - 15:00', 'CONFIRMED', 200000.00, 'Health check for Luna', NOW());

-- BookingServices
INSERT INTO BookingServices (booking_id, service_id) VALUES
(1, 1),
(2, 3);

-- Orders
INSERT INTO Orders (
    customer_id, order_code, total_amount, status, created_at, updated_at
) VALUES
(2, 'ORD-20260316-001', 320000.00, 'PENDING',   '2026-03-10 09:15:00', '2026-03-10 09:15:00'),
(3, 'ORD-20260316-002', 305000.00, 'CONFIRMED', '2026-03-11 14:20:00', '2026-03-11 15:00:00'),
(4, 'ORD-20260316-003', 225000.00, 'SHIPPING',  '2026-03-12 10:05:00', '2026-03-12 16:30:00'),
(2, 'ORD-20260316-004', 500000.00, 'COMPLETED', '2026-03-13 11:45:00', '2026-03-14 08:15:00'),
(3, 'ORD-20260316-005', 70000.00,  'CANCELLED', '2026-03-14 17:10:00', '2026-03-14 18:00:00');

-- OrderItems
INSERT INTO OrderItems (
    order_id, product_id, quantity, unit_price, sub_total
) VALUES
(1, 1, 1, 250000.00, 250000.00),
(1, 4, 1, 70000.00, 70000.00),

(2, 3, 1, 95000.00, 95000.00),
(2, 6, 1, 120000.00, 120000.00),
(2, 5, 2, 45000.00, 90000.00),

(3, 2, 1, 180000.00, 180000.00),
(3, 5, 1, 45000.00, 45000.00),

(4, 1, 1, 250000.00, 250000.00),
(4, 2, 1, 180000.00, 180000.00),
(4, 4, 1, 70000.00, 70000.00),

(5, 4, 1, 70000.00, 70000.00);

-- PaymentHistories
INSERT INTO PaymentHistories (
    order_id, amount, payment_method, payment_status, payment_date
) VALUES
(2, 305000.00, 'CASH', TRUE, '2026-03-11 15:10:00'),
(3, 225000.00, 'BANK_TRANSFER', FALSE, '2026-03-12 16:35:00'),
(4, 500000.00, 'VNPAY', TRUE, '2026-03-14 08:20:00');

-- Feedbacks
INSERT INTO Feedbacks (
    customer_id, rating, comment, created_at
) VALUES
(2, 5, 'Dich vu rat tot, nhan vien than thien.', NOW()),
(3, 4, 'San pham on, giao hang nhanh.', NOW()),
(4, 5, 'Thu cung duoc cham soc rat ky.', NOW());