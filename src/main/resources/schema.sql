drop table orders if exists;

CREATE TABLE orders(
                       orderId INT PRIMARY KEY,
                       customerName VARCHAR(100),
                       amount DOUBLE
);