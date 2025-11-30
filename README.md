🛒 E-Commerce Backend (Spring Boot) 
 
A clean, modular e-commerce backend built with Spring Boot, JPA, Hibernate, and MySQL. 
Supports product management, cart system, orders, and user accounts with full validation and centralized exception handling. 
 
🚀 Features 
🔐 User Management 
 
Register user 
 
Update user profile 
 
Get user by ID or email 
 
Delete user (with cascade safety) 
 
🛍 Product Management 
 
Create / update / delete products 
 
Search products by name 
 
Filter by price range 
 
List available products 
 
Prevents invalid stock operations 
 
🧺 Cart System 
 
Add item to cart 
 
Update item quantity 
 
Remove items 
 
Clear cart 
 
Always recalculates totals 
 
Stock-aware validation 
 
📦 Order Processing 
 
Place order from cart 
 
Reduce stock on order 
 
Restore stock on cancellation 
 
List user orders 
 
Order pagination 
 
Fetch order items 
 
Date-range filtering 
 
⚠️ Centralized Error Handling 
 
Handled via GlobalExceptionHandler: 
 
Validation errors 
 
Resource not found 
 
Stock issues 
 
Generic exceptions 
 
Produces a consistent ApiResponse<T> for all endpoints. 
 
🧱 Tech Stack 
Backend 
 
Java (17+ recommended) 
 
Spring Boot 
 
Spring Web (REST) 
 
Spring Data JPA 
 
Hibernate ORM 
 
Spring Validation 
 
Spring Transaction Management 
 
Database 
 
MySQL (recommended) 
 
JPA/Hibernate schema generation 
 
Tools & Utilities 
 
Maven 
 
Jackson (JSON serialization) 
 
Lombok (optional — not used yet but recommended) 
 
📁 Project Structure 
src/main/java/com/ecommerce 
│ 
├── controller        # REST controllers 
│   ├── CartController 
│   ├── ProductController 
│   ├── OrderController 
│   └── UserController 
│ 
├── dto               # DTOs used in APIs 
│   ├── ApiResponse 
│   ├── AddToCartRequest 
│   ├── ProductDto 
│   ├── UserDto 
│   ├── CartDto 
│   └── CartItemDto 
│ 
├── entity            # JPA entities 
│   ├── User 
│   ├── Product 
│   ├── Cart 
│   ├── CartItem 
│   ├── Order 
│   └── OrderItem 
│ 
├── exception         # Custom exceptions + global handler 
│   ├── ResourceNotFoundException 
│   ├── InsufficientStockException 
│   └── GlobalExceptionHandler 
│ 
├── repository        # Spring Data JPA repositories 
│ 
└── service           # Business logic 
   ├── CartService 
   ├── UserService 
   ├── ProductService 
   └── OrderService 
 
🗄 Database Schema 
Users (1) ──── (1) Cart 
Users (1) ──── (∞) Orders 
Cart (1) ──── (∞) CartItems 
Product (1) ──── (∞) OrderItems 
Product (1) ──── (∞) CartItems 
Orders (1) ──── (∞) OrderItems 
 
Key Table Fields 
 
User: id, name, email, address, createdAt 
 
Product: id, name, description, price, stockQuantity, timestamps 
 
Cart: id, userId, totalAmount 
 
Order: id, userId, orderDate, totalAmount 
 
CartItem: id, cartId, productId, quantity, subtotal 
 
OrderItem: id, orderId, productId, quantity, price 
 
🛠 How to Run the Project 
1. Clone the repo 
git clone https://github.com/arijitnandi07/ecommerce-backend
cd ecommerce-backend 
 
2. Configure Application Properties 
 
src/main/resources/application.properties: 
 
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce 
spring.datasource.username=root 
spring.datasource.password=your_password 
 
spring.jpa.hibernate.ddl-auto=update 
spring.jpa.show-sql=true 
spring.jpa.properties.hibernate.format_sql=true 
 
3. Run MySQL 
 
Create the database: 
 
CREATE DATABASE ecommerce; 
 
4. Run the application 
 
Using Maven: 
 
mvn spring-boot:run 
 
 
Or run from your IDE. 
 
🔗 API Endpoints Overview 
User 
POST   /v1/users/register 
GET    /v1/users/{id} 
GET    /v1/users/email/{email} 
PUT    /v1/users/{id} 
DELETE /v1/users/{id} 
 
Products 
POST   /api/v1/products 
GET    /api/v1/products 
GET    /api/v1/products/{id} 
PUT    /api/v1/products/{id} 
DELETE /api/v1/products/{id} 
GET    /api/v1/products/search?name= 
GET    /api/v1/products/price-range?minPrice=&maxPrice= 
 
Cart 
POST   /api/v1/cart/add/{userId} 
GET    /api/v1/cart/{userId} 
GET    /api/v1/cart/items/{userId} 
PUT    /api/v1/cart/item/{cartItemId}?quantity= 
DELETE /api/v1/cart/item/{cartItemId} 
DELETE /api/v1/cart/clear/{userId} 
 
Orders 
POST   /api/v1/orders/place/{userId} 
GET    /api/v1/orders/{orderId} 
GET    /api/v1/orders/user/{userId} 
GET    /api/v1/orders/user/{userId}/paged?page=&size= 
GET    /api/v1/orders/{orderId}/items 
GET    /api/v1/orders/date-range?startDate=&endDate= 
DELETE /api/v1/orders/{orderId} 
 
📬 Sample API Response (Standardized) 
{ 
 "success": true, 
 "message": "Product created successfully", 
 "data": { 
   "id": 1, 
   "name": "Laptop", 
   "price": 1500.00, 
   "stockQuantity": 10 
 } 
} 
 
 
Consistent for every endpoint. 
 
📌 TODO / Future Improvements 
 
Add authentication (JWT) 
 
Replace entity exposure with DTOs everywhere 
 
Add pagination to all list APIs 
 
Add OrderStatus instead of deleting orders 
 
Prevent deleting products with order history 
 
Introduce optimistic locking for stock safety 
 
Add soft delete for users/products 
 
Add audit logging 
