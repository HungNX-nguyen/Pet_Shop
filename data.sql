-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: petlover
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accountroles`
--

DROP TABLE IF EXISTS `accountroles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accountroles` (
  `account_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`account_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `accountroles_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `accountroles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accountroles`
--

LOCK TABLES `accountroles` WRITE;
/*!40000 ALTER TABLE `accountroles` DISABLE KEYS */;
INSERT INTO `accountroles` VALUES (1,1),(2,1),(3,2),(4,2),(5,2),(6,2);
/*!40000 ALTER TABLE `accountroles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'owner1','Nguyen Van A','owner1@gmail.com','123456','0900000001','2026-03-17 00:56:44','2026-03-17 00:56:44',1),(2,'owner2','Tran Van B','owner2@gmail.com','123456','0900000002','2026-03-17 00:56:44','2026-03-17 00:56:44',1),(3,'customer1','Le Thi C','customer1@gmail.com','123456','0900000003','2026-03-17 00:56:44','2026-03-17 00:56:44',1),(4,'customer2','Pham Van D','customer2@gmail.com','123456','0900000004','2026-03-17 00:56:44','2026-03-17 00:56:44',1),(5,'customer3','Hoang Thi E','customer3@gmail.com','123456','0900000005','2026-03-17 00:56:44','2026-03-17 00:56:44',1),(6,'hung','hung','hung@gmail.com','$2a$10$7gEXzPC.T7IsrRMxanldh.0IVimJe2Y3TqhqrzhVz889f.7i5eIwS',NULL,'2026-03-17 00:57:58','2026-03-17 00:57:58',1);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `pet_id` int DEFAULT NULL,
  `booking_code` varchar(255) DEFAULT NULL,
  `booking_date` date DEFAULT NULL,
  `time_slot` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_price` decimal(10,2) DEFAULT NULL,
  `note` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  KEY `pet_id` (`pet_id`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`pet_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,3,NULL,'BK001','2026-03-20','09:00','CONFIRMED',30.00,'Grooming for Milo','2026-03-17 00:56:44'),(2,4,NULL,'BK002','2026-03-21','10:00','PENDING',20.00,'Bath for Rocky','2026-03-17 00:56:44'),(3,6,NULL,'PS-3CA61','2026-03-22',NULL,'CANCELLED',25.00,',','2026-03-17 01:50:19');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookingservices`
--

DROP TABLE IF EXISTS `bookingservices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookingservices` (
  `booking_id` int NOT NULL,
  `service_id` int NOT NULL,
  PRIMARY KEY (`booking_id`,`service_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `bookingservices_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `bookingservices_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookingservices`
--

LOCK TABLES `bookingservices` WRITE;
/*!40000 ALTER TABLE `bookingservices` DISABLE KEYS */;
INSERT INTO `bookingservices` VALUES (1,1),(2,2),(3,3);
/*!40000 ALTER TABLE `bookingservices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cartitems`
--

DROP TABLE IF EXISTS `cartitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cartitems` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_cart_product` (`cart_id`,`product_id`),
  UNIQUE KEY `UKqd379rynxr775d834pma6qhai` (`cart_id`,`product_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cartitems_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`),
  CONSTRAINT `cartitems_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cartitems`
--

LOCK TABLES `cartitems` WRITE;
/*!40000 ALTER TABLE `cartitems` DISABLE KEYS */;
/*!40000 ALTER TABLE `cartitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customer_id` (`customer_id`),
  CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,6,'2026-03-17 00:58:15','2026-03-17 00:58:15');
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Dog Food','Food for dogs'),(2,'Cat Food','Food for cats'),(3,'Pet Toys','Toys for pets'),(4,'Accessories','Pet accessories'),(5,'Health Care','Pet health products');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `product_id` int NOT NULL,
  `order_id` int NOT NULL,
  `rating` int DEFAULT NULL,
  `comment` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_feedback` (`customer_id`,`product_id`,`order_id`),
  UNIQUE KEY `UKl9ytnic3lbvb50takd9t26c7e` (`customer_id`,`product_id`,`order_id`),
  KEY `product_id` (`product_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `feedbacks_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `feedbacks_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `feedbacks_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitems`
--

DROP TABLE IF EXISTS `orderitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitems` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `unit_price` decimal(38,2) DEFAULT NULL,
  `sub_total` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitems`
--

LOCK TABLES `orderitems` WRITE;
/*!40000 ALTER TABLE `orderitems` DISABLE KEYS */;
INSERT INTO `orderitems` VALUES (1,1,1,2,20.00,40.00),(2,2,5,3,8.00,24.00),(3,2,7,1,12.00,12.00),(4,3,10,2,14.00,28.00),(5,3,6,4,6.00,24.00),(6,4,18,2,30000.00,60000.00),(7,5,1,2,20000.00,40000.00),(8,6,18,3,30000.00,90000.00),(9,7,17,1,9000.00,9000.00),(10,8,13,1,23000.00,23000.00),(11,9,10,1,14000.00,14000.00),(12,10,13,1,23000.00,23000.00),(13,11,2,1,25000.00,25000.00),(14,12,2,2,25000.00,50000.00),(15,13,2,1,25000.00,25000.00),(16,14,18,2,30000.00,60000.00),(17,15,1,2,20000.00,40000.00),(18,16,6,1,6000.00,6000.00);
/*!40000 ALTER TABLE `orderitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `order_code` varchar(255) DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,3,'ORD001',40.00,'PAID',NULL,'2026-03-17 00:56:44',NULL),(2,4,'ORD002',36.00,'PENDING',NULL,'2026-03-17 00:56:44',NULL),(3,5,'ORD003',52.00,'PAID',NULL,'2026-03-17 00:56:44',NULL),(4,6,'ORD-A2FB7E51',63005.99,'PAID','Alex Ferguson | jgf, 13131 | 0867674841','2026-03-17 00:58:19','2026-03-17 00:58:59'),(5,6,'ORD-F423EAD3',42005.99,'CANCELLED','Hùng Nguyễn Xuân | Ha Dong, 1313 | 0867674841','2026-03-17 01:04:38','2026-03-17 01:11:37'),(6,6,'ORD-262C3A2B',94505.99,'PAID','Alex Ferguson | jgf, 31313 | 0867674841','2026-03-17 01:29:51','2026-03-17 01:30:48'),(7,6,'ORD-FB0A7E72',9455.99,'PROCESSING','Hùng Nguyễn Xuân | Ha Dong, 313 | 0867674841','2026-03-17 01:33:44','2026-03-17 01:33:51'),(8,6,'ORD-7D46E0B7',24155.99,'WAITING_PAYMENT',NULL,'2026-03-17 01:34:14','2026-03-17 01:34:14'),(9,6,'ORD-CF6CD76E',14705.99,'PROCESSING','3131 | 31313, fff | 0867674842','2026-03-17 01:48:58','2026-03-17 01:54:09'),(10,6,'ORD-579C2F31',24155.99,'PAID','2`2 | `2`, 1313 | 1391381838','2026-03-17 01:49:08','2026-03-17 01:49:41'),(11,6,'ORD-B9571DF4',26255.99,'CANCELLED','313 | âda, ffffff | 1391381838','2026-03-17 01:58:59','2026-03-17 01:59:30'),(12,6,'ORD-F4EE0F00',52505.99,'CANCELLED',NULL,'2026-03-17 02:02:10','2026-03-17 02:02:36'),(13,6,'ORD-A424584B',26255.99,'CANCELLED','James Ancelooti | 313, 31313 | 94194141','2026-03-17 02:02:50','2026-03-17 02:03:18'),(14,6,'ORD-57BF063A',63005.99,'CANCELLED',NULL,'2026-03-17 02:13:02','2026-03-17 02:13:42'),(15,6,'ORD-4DA6F3B3',42005.99,'CANCELLED','Alex Ferguson | jgf, 3131313 | 0867674841','2026-03-17 02:13:16','2026-03-17 02:13:29'),(16,6,'ORD-B894CDEF',6305.99,'PROCESSING','Alex Ferguson | jgf, 31313 | 0867674841','2026-03-17 02:35:09','2026-03-17 02:35:40');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paymenthistories`
--

DROP TABLE IF EXISTS `paymenthistories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paymenthistories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `payment_status` tinyint(1) DEFAULT NULL,
  `payment_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`),
  CONSTRAINT `paymenthistories_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paymenthistories`
--

LOCK TABLES `paymenthistories` WRITE;
/*!40000 ALTER TABLE `paymenthistories` DISABLE KEYS */;
INSERT INTO `paymenthistories` VALUES (1,1,40.00,'CASH',1,'2026-03-17 00:56:44'),(2,3,52.00,'BANK_TRANSFER',1,'2026-03-17 00:56:44'),(3,4,63005.99,'ONLINE',1,'2026-03-17 00:58:59'),(4,5,42005.99,'COD',0,NULL),(5,6,94505.99,'ONLINE',1,'2026-03-17 01:30:48'),(6,7,9455.99,'COD',0,NULL),(7,8,24155.99,'COD',0,NULL),(8,9,14705.99,'COD',0,NULL),(9,10,24155.99,'ONLINE',1,'2026-03-17 01:49:41'),(10,11,26255.99,'ONLINE',0,NULL),(11,12,52505.99,'ONLINE',0,NULL),(12,13,26255.99,'ONLINE',0,NULL),(13,14,63005.99,'COD',0,NULL),(14,15,42005.99,'ONLINE',0,NULL),(15,16,6305.99,'COD',0,NULL);
/*!40000 ALTER TABLE `paymenthistories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
  `pet_id` int NOT NULL AUTO_INCREMENT,
  `owner_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `breed` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`pet_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES (1,3,'Milo','Dog','Poodle','Male',4.2,2),(2,3,'Luna','Cat','British Shorthair','Female',3.1,1),(3,4,'Rocky','Dog','Bulldog','Male',10.5,3),(4,5,'Kitty','Cat','Persian','Female',2.9,2),(5,5,'Tom','Cat','Maine Coon','Male',5.3,4);
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_by` int NOT NULL,
  `category_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `price` decimal(38,2) DEFAULT NULL,
  `stock_quantity` int DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,1,1,'Premium Dog Food','High quality dog food',20000.00,98,'dogfood1.jpg',1),(2,1,1,'Organic Dog Food','Healthy dog food',25000.00,78,'dogfood2.jpg',1),(3,1,2,'Salmon Cat Food','Salmon flavor cat food',18000.00,90,'catfood1.jpg',1),(4,1,2,'Tuna Cat Food','Tuna flavor cat food',17000.00,100,'catfood2.jpg',1),(5,1,3,'Rubber Dog Ball','Durable dog toy',8000.00,150,'toy1.jpg',1),(6,1,3,'Cat Feather Toy','Interactive toy',6000.00,199,'toy2.jpg',1),(7,1,4,'Dog Collar','Adjustable collar',12000.00,120,'collar.jpg',1),(8,1,4,'Cat Collar','Cute cat collar',10000.00,140,'catcollar.jpg',1),(9,1,4,'Dog Leash','Strong dog leash',15000.00,90,'leash.jpg',1),(10,1,5,'Pet Shampoo','Gentle shampoo',14000.00,59,'shampoo.jpg',1),(11,1,5,'Flea Treatment','Anti flea medicine',22000.00,50,'flea.jpg',1),(12,2,1,'Puppy Food','Food for puppies',19000.00,70,'puppyfood.jpg',1),(13,2,1,'Senior Dog Food','Food for old dogs',23000.00,58,'seniordog.jpg',1),(14,2,2,'Kitten Food','Food for kittens',16000.00,85,'kitten.jpg',1),(15,2,2,'Indoor Cat Food','Food for indoor cats',18000.00,75,'indoorcat.jpg',1),(16,2,3,'Chew Bone Toy','Dog chew toy',7000.00,130,'chewbone.jpg',1),(17,2,3,'Laser Cat Toy','Laser toy for cats',9000.00,109,'laser.jpg',1),(18,2,4,'Pet Bed','Soft pet bed',30000.00,35,'bed.jpg',1),(19,2,4,'Travel Pet Bag','Portable pet bag',35000.00,30,'bag.jpg',1),(20,2,5,'Vitamin Supplement','Pet vitamins',28000.00,45,'vitamin.jpg',1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'CUSTOMER'),(1,'SHOP_OWNER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_by` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `services_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,1,'Pet Grooming','Full grooming service',30.00,60,'2026-03-17 00:56:44'),(2,1,'Pet Bath','Bath and cleaning',20.00,40,'2026-03-17 00:56:44'),(3,2,'Pet Haircut','Professional haircut',25.00,45,'2026-03-17 00:56:44'),(4,2,'Pet Nail Trim','Nail trimming service',10.00,15,'2026-03-17 00:56:44');
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-17  2:48:29
