AgriConnect – Farmer Supply Chain Management System

AgriConnect is a desktop-based farmer supply chain management system developed using Java Swing and MySQL. The system connects farmers, customers, distributors, and administrators on a single platform to manage agricultural products, orders, deliveries, and user accounts.

The project aims to simplify the process of selling agricultural products by providing separate dashboards and functionalities for each type of user.

Features
👨‍🌾 Farmer Module
Farmer login and authentication
Farmer dashboard
Add and manage agricultural products
View product details
Manage available product quantity
Track products associated with the farmer
🛒 Customer Module
Customer registration and login
Browse available agricultural products
Add products to cart
Remove products from cart
Calculate order subtotal
Automatic 5% GST calculation
Platform fee calculation
Place orders
View order history
Generate/view bills
🚚 Distributor Module
Distributor login
View assigned orders
Track order status
Mark orders as delivered
Maintain delivery history
👨‍💼 Admin Module
Admin login
Admin dashboard
Manage farmers
Manage customers
Manage distributors
Manage agricultural products
Manage orders
View account-related information
System Workflow
                    ┌──────────────────┐
                    │      Admin       │
                    └────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          ↓                  ↓                  ↓
     Manage Farmers    Manage Customers   Manage Distributors
          │                  │                  │
          └──────────────────┼──────────────────┘
                             ↓
                    ┌──────────────────┐
                    │    Products      │
                    │  farmer_products │
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │    Customer      │
                    │ Browse Products  │
                    │   Add to Cart    │
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │   Place Order    │
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │    Distributor   │
                    │  Assigned Order  │
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │     Delivered    │
                    └────────┬─────────┘
                             ↓
                    ┌──────────────────┐
                    │  Order History   │
                    └──────────────────┘
Technology Stack
Technology	Purpose
Java	Core application development
Java Swing	Desktop graphical user interface
MySQL	Database management
JDBC	Java–MySQL database connectivity
Jakarta Mail	Email functionality
Gson	JSON processing
JCalendar	Date/calendar components
JGoodies	UI-related components
JUnit	Testing support
Project Structure
FinalMiniProject/
│
├── AdminDashboard.java
├── AdminLogin.java
├── AdminOrdersPage.java
│
├── FarmerDashboard.java
├── FarmerLogin.java
├── ManageFarmers.java
│
├── CustomerDashboard.java
├── CustomerLogin.java
├── SignUp.java
├── CartPage.java
├── BillPage.java
├── OrderHistoryPage.java
│
├── DistributorDashboard.java
├── DistributorLogin.java
│
├── ManageCustomers.java
├── ManageDistributors.java
├── ManageProducts.java
├── AccountsPage.java
├── PendingOrdersPage.java
├── LoginPage.java
│
├── SendEmail.java
├── dependency.txt
│
├── jcalendar-1.4.jar
├── jgoodies-common-1.2.0.jar
├── jgoodies-looks-2.4.1.jar
├── junit-4.6.jar
├── gson-2.11.0.jar
├── jm.jar
│
└── Images/
    ├── cabbage.jpeg
    ├── carrots-1851424_1280.jpg
    ├── potatoes-116008750-596413603df78cdc68c061f3.jpg
    └── ...
Database

The application uses MySQL with a database named:

agriconnect

The Java application connects using JDBC.

The main database entities used by the application include:

admins
farmers
customers
distributors
farmer_products
orders
order_items
order_history
Basic Database Setup

Create the database in MySQL:

CREATE DATABASE agriconnect;

Then create/import the required tables before running the application.

Note: The current source code contains a local MySQL username and password. For security, these credentials should be moved to a configuration file or environment variables before publishing the project publicly.

Requirements

Before running the project, install:

Java JDK 8 or later
MySQL Server
A Java IDE such as:
IntelliJ IDEA
Eclipse
NetBeans
Required .jar libraries included in the repository
MySQL JDBC Driver
How to Run
1. Clone the Repository
git clone https://github.com/yourusername/AgriConnect.git

Navigate to the project directory:

cd AgriConnect
2. Configure MySQL

Start MySQL and create the database:

CREATE DATABASE agriconnect;

Create/import all required tables and insert the required initial records.

3. Configure Database Connection

The application currently uses JDBC connections similar to:

jdbc:mysql://localhost:3306/agriconnect

Update the username and password in the Java source files according to your local MySQL configuration.

4. Add Required Libraries

Add the provided .jar files to the project's classpath.

The project includes libraries such as:

jcalendar-1.4.jar
jgoodies-common-1.2.0.jar
jgoodies-looks-2.4.1.jar
gson-2.11.0.jar
junit-4.6.jar
jm.jar

For email functionality, the project also requires:

Jakarta Mail 2.0.1
5. Run the Application

Start the application from the appropriate login/main class in your IDE.

The available modules can then be accessed through their respective login pages.

Order Processing

The order process follows these steps:

Customer logs into the system.
Customer browses available farmer products.
Customer selects products and quantities.
Products are added to the shopping cart.
The system calculates:
Subtotal
5% GST
Platform fee
Final order total
Customer places the order.
Order is stored in the MySQL database.
Distributor receives the assigned order.
Distributor updates the order status.
Once delivered, the order is recorded in order history.
Customer can view previous orders.
User Roles
User	Main Responsibilities
Admin	Manage users, products, and orders
Farmer	Manage agricultural products
Customer	Browse products and place orders
Distributor	Handle assigned orders and deliveries
Key Java Concepts Used

This project demonstrates several Java concepts, including:

Object-Oriented Programming
Classes and Objects
Inheritance
Event Handling
Exception Handling
Java Swing GUI
JDBC
SQL Queries
Prepared Statements
Database Transactions
JTable and GUI components
Modular application design
Security Considerations

For a production-ready version, the following improvements are recommended:

Store database credentials using environment variables.
Never commit email passwords or API credentials to GitHub.
Hash user passwords instead of storing plain-text passwords.
Use configuration files for database settings.
Validate user input before executing database operations.
Use proper role-based authorization.
Remove sensitive credentials from source code.

Important: If any real Gmail App Password has ever been committed to a public repository, revoke it and generate a new one.

Future Enhancements

Possible improvements for future versions include:

Web-based or mobile interface
Online payment integration
Real-time order tracking
Product search and filtering
Farmer analytics dashboard
Sales and revenue reports
Inventory alerts
SMS/email order notifications
Product reviews and ratings
Cloud database integration
REST API backend
Secure authentication with password hashing
Data visualization for sales and supply-chain analysis
Project Objective

The primary objective of AgriConnect is to provide a centralized platform for managing the agricultural supply chain. It helps connect farmers with customers while providing distributors with an organized method of handling deliveries and administrators with tools to manage the overall system.

Contributors

Developed as a Mini Project

Java
MySQL
Java Swing
JDBC
