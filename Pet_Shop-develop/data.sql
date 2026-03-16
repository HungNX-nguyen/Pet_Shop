DROP DATABASE IF EXISTS petlover;
CREATE DATABASE petlover;
USE petlover;

------------------------------------------------
-- ACCOUNT
------------------------------------------------
CREATE TABLE Accounts (
    accountId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP 
              ON UPDATE CURRENT_TIMESTAMP,
    isActive BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

------------------------------------------------
-- ROLES
------------------------------------------------
CREATE TABLE Roles (
    roleId INT AUTO_INCREMENT PRIMARY KEY,
    roleName VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB;

INSERT INTO Roles(roleName) VALUES
('SHOP_OWNER'),
('CUSTOMER');

------------------------------------------------
-- ACCOUNT ROLES
------------------------------------------------
CREATE TABLE AccountRoles (
    accountId INT,
    roleId INT,
    PRIMARY KEY (accountId, roleId),
    FOREIGN KEY (accountId) REFERENCES Accounts(accountId),
    FOREIGN KEY (roleId) REFERENCES Roles(roleId)
) ENGINE=InnoDB;

------------------------------------------------
-- PETS
------------------------------------------------
CREATE TABLE Pets (
    petId INT AUTO_INCREMENT PRIMARY KEY,
    ownerId INT NOT NULL,
    name VARCHAR(100),
    type VARCHAR(100),
    breed VARCHAR(100),
    gender VARCHAR(10),
    weight FLOAT,
    age INT,
    FOREIGN KEY (ownerId) REFERENCES Accounts(accountId)
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
    createdBy INT NOT NULL,
    categoryId INT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    stockQuantity INT,
    imageUrl VARCHAR(500),
    isActive BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (createdBy) REFERENCES Accounts(accountId),
    FOREIGN KEY (categoryId) REFERENCES Categories(id)
) ENGINE=InnoDB;

------------------------------------------------
-- SERVICES
------------------------------------------------
CREATE TABLE Services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    createdBy INT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    duration INT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (createdBy) REFERENCES Accounts(accountId)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKINGS
------------------------------------------------
CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customerId INT NOT NULL,
    bookingCode VARCHAR(50),
    bookingDate DATE,
    timeSlot VARCHAR(50),
    status VARCHAR(50),
    totalPrice DECIMAL(10,2),
    note TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customerId) REFERENCES Accounts(accountId)
) ENGINE=InnoDB;

------------------------------------------------
-- BOOKING SERVICES
------------------------------------------------
CREATE TABLE BookingServices (
    bookingId INT,
    serviceId INT,
    PRIMARY KEY (bookingId, serviceId),
    FOREIGN KEY (bookingId) REFERENCES Bookings(id),
    FOREIGN KEY (serviceId) REFERENCES Services(id)
) ENGINE=InnoDB;

------------------------------------------------
-- ORDERS
------------------------------------------------
CREATE TABLE Orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customerId INT NOT NULL,
    orderCode VARCHAR(50),
    totalAmount DECIMAL(10,2),
    status VARCHAR(50),
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME,
    FOREIGN KEY (customerId) REFERENCES Accounts
    (accountId)
) ENGINE=InnoDB;

------------------------------------------------
-- ORDER ITEMS
------------------------------------------------
CREATE TABLE OrderItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT,
    unitPrice DECIMAL(10,2),
    subTotal DECIMAL(10,2),
    FOREIGN KEY (orderId) REFERENCES Orders(id),
    FOREIGN KEY (productId) REFERENCES Products(id)
) ENGINE=InnoDB;

------------------------------------------------
-- PAYMENT HISTORIES
------------------------------------------------
CREATE TABLE PaymentHistories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT UNIQUE,
    amount DECIMAL(10,2),
    paymentMethod VARCHAR(100),
    paymentStatus BOOLEAN,
    paymentDate DATETIME,
    FOREIGN KEY (orderId) REFERENCES Orders(id)
) ENGINE=InnoDB;

------------------------------------------------
-- FEEDBACKS
------------------------------------------------
CREATE TABLE Feedbacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customerId INT,
    rating INT,
    comment TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customerId) REFERENCES Accounts(accountId)
) ENGINE=InnoDB;


