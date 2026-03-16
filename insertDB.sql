USE petlover;

------------------------------------------------
-- ACCOUNTS
------------------------------------------------
INSERT INTO Accounts(username, full_name, email, password, phone_number) VALUES
('owner1','Nguyen Van A','owner1@gmail.com','123456','0900000001'),
('owner2','Tran Van B','owner2@gmail.com','123456','0900000002'),
('customer1','Le Thi C','customer1@gmail.com','123456','0900000003'),
('customer2','Pham Van D','customer2@gmail.com','123456','0900000004'),
('customer3','Hoang Thi E','customer3@gmail.com','123456','0900000005');

------------------------------------------------
-- ACCOUNT ROLES
------------------------------------------------
INSERT INTO AccountRoles VALUES
(1,1),
(2,1),
(3,2),
(4,2),
(5,2);

------------------------------------------------
-- CATEGORIES
------------------------------------------------
INSERT INTO Categories(name,description) VALUES
('Dog Food','Food for dogs'),
('Cat Food','Food for cats'),
('Pet Toys','Toys for pets'),
('Accessories','Pet accessories'),
('Health Care','Pet health products');

------------------------------------------------
-- PETS
------------------------------------------------
INSERT INTO Pets(owner_id,name,type,breed,gender,weight,age) VALUES
(3,'Milo','Dog','Poodle','Male',4.2,2),
(3,'Luna','Cat','British Shorthair','Female',3.1,1),
(4,'Rocky','Dog','Bulldog','Male',10.5,3),
(5,'Kitty','Cat','Persian','Female',2.9,2),
(5,'Tom','Cat','Maine Coon','Male',5.3,4);

------------------------------------------------
-- PRODUCTS (20 PRODUCTS)
------------------------------------------------
INSERT INTO Products(created_by,category_id,name,description,price,stock_quantity,image_url) VALUES
(1,1,'Premium Dog Food','High quality dog food',20000,100,'dogfood1.jpg'),
(1,1,'Organic Dog Food','Healthy dog food',25000,80,'dogfood2.jpg'),
(1,2,'Salmon Cat Food','Salmon flavor cat food',18000,90,'catfood1.jpg'),
(1,2,'Tuna Cat Food','Tuna flavor cat food',17000,100,'catfood2.jpg'),
(1,3,'Rubber Dog Ball','Durable dog toy',8000,150,'toy1.jpg'),
(1,3,'Cat Feather Toy','Interactive toy',6000,200,'toy2.jpg'),
(1,4,'Dog Collar','Adjustable collar',12000,120,'collar.jpg'),
(1,4,'Cat Collar','Cute cat collar',10000,140,'catcollar.jpg'),
(1,4,'Dog Leash','Strong dog leash',15000,90,'leash.jpg'),
(1,5,'Pet Shampoo','Gentle shampoo',14000,60,'shampoo.jpg'),
(1,5,'Flea Treatment','Anti flea medicine',22000,50,'flea.jpg'),
(2,1,'Puppy Food','Food for puppies',19000,70,'puppyfood.jpg'),
(2,1,'Senior Dog Food','Food for old dogs',23000,60,'seniordog.jpg'),
(2,2,'Kitten Food','Food for kittens',16000,85,'kitten.jpg'),
(2,2,'Indoor Cat Food','Food for indoor cats',18000,75,'indoorcat.jpg'),
(2,3,'Chew Bone Toy','Dog chew toy',7000,130,'chewbone.jpg'),
(2,3,'Laser Cat Toy','Laser toy for cats',9000,110,'laser.jpg'),
(2,4,'Pet Bed','Soft pet bed',30000,40,'bed.jpg'),
(2,4,'Travel Pet Bag','Portable pet bag',35000,30,'bag.jpg'),
(2,5,'Vitamin Supplement','Pet vitamins',28000,45,'vitamin.jpg');

------------------------------------------------
-- SERVICES
------------------------------------------------
INSERT INTO Services(created_by,name,description,price,duration) VALUES
(1,'Pet Grooming','Full grooming service',30,60),
(1,'Pet Bath','Bath and cleaning',20,40),
(2,'Pet Haircut','Professional haircut',25,45),
(2,'Pet Nail Trim','Nail trimming service',10,15);

------------------------------------------------
-- BOOKINGS
------------------------------------------------
INSERT INTO Bookings(customer_id,booking_code,booking_date,time_slot,status,total_price,note) VALUES
(3,'BK001','2026-03-20','09:00','CONFIRMED',30,'Grooming for Milo'),
(4,'BK002','2026-03-21','10:00','PENDING',20,'Bath for Rocky');

------------------------------------------------
-- BOOKING SERVICES
------------------------------------------------
INSERT INTO BookingServices VALUES
(1,1),
(2,2);

------------------------------------------------
-- ORDERS
------------------------------------------------
INSERT INTO Orders(customer_id,order_code,total_amount,status) VALUES
(3,'ORD001',40,'PAID'),
(4,'ORD002',36,'PENDING'),
(5,'ORD003',52,'PAID');

------------------------------------------------
-- ORDER ITEMS
------------------------------------------------
INSERT INTO OrderItems(order_id,product_id,quantity,unit_price,sub_total) VALUES
(1,1,2,20,40),
(2,5,3,8,24),
(2,7,1,12,12),
(3,10,2,14,28),
(3,6,4,6,24);

------------------------------------------------
-- PAYMENT HISTORIES
------------------------------------------------
INSERT INTO PaymentHistories(order_id,amount,payment_method,payment_status,payment_date) VALUES
(1,40,'CASH',TRUE,NOW()),
(3,52,'BANK_TRANSFER',TRUE,NOW());

