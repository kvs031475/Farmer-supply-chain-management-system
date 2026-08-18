# Digital Supply Chain Management System

## Project Overview

The **Digital Supply Chain Management System** is a Java and MySQL-based system developed to manage the movement of agricultural products between **farmers, administrators, distributors, and consumers**.

The project provides separate modules for each participant in the supply chain. Instead of handling product listing, quality checking, transportation, ordering, and feedback separately, the system brings these activities together into one platform.

The main focus of the project is to improve **product management, order management, logistics coordination, and communication between supply-chain participants**.

## Problem Addressed

The traditional agricultural supply chain can involve multiple intermediaries between farmers and consumers. This can create difficulties in:

* Managing product information
* Maintaining transparent pricing
* Coordinating transportation and storage
* Tracking orders
* Maintaining product quality
* Collecting consumer feedback

This project provides a centralized system to manage these activities.

---

# System Modules

## 1. Farmer Module

The farmer is the starting point of the supply chain.

The farmer can:

* Upload available agricultural produce
* Add product details
* Set the price of the produce
* Manage listed products
* Track customer orders

### Farmer Flow

**Farmer Login → Add Produce → Enter Product Details → Set Price → Product Available for Consumers → Receive/Track Orders**

Once a farmer adds a product, the product becomes part of the system and can proceed through the supply chain.

---

## 2. Admin Module

The admin acts as the central management component of the system.

The admin is responsible for:

* Managing system operations
* Performing quality checks
* Coordinating logistics
* Managing information between different modules

### Admin Flow

**Admin Login → View Products/Orders → Quality Check → Coordinate Logistics → Monitor Operations**

The admin helps ensure that products and orders move properly between the different participants.

---

## 3. Distributor Module

The distributor handles the movement and storage of agricultural products.

The distributor manages:

* Transportation
* Product movement
* Storage coordination
* Delivery-related activities

### Distributor Flow

**Receive Product/Order Information → Plan Transportation → Manage Storage → Move Product → Support Delivery**

This module connects the product supplied by the farmer with the consumer's order.

---

## 4. Consumer Module

The consumer is the final buyer in the supply chain.

The consumer can:

* Browse available agricultural products
* View product information
* Place orders
* Process payments
* Provide ratings and feedback

### Consumer Flow

**Browse Products → Select Product → Place Order → Payment → Product Distribution → Receive Product → Give Feedback**

The consumer therefore interacts with the system from product selection until the completion of the order.

---

## 5. Feedback Module

The feedback system collects information from consumers after their purchase.

Consumers can provide:

* Product ratings
* Quality feedback
* General feedback about their purchase

This feedback can be used to support continuous improvement of product and service quality.

### Feedback Flow

**Order Completed → Consumer Provides Rating/Feedback → Feedback Recorded → Quality Improvement**

---

# Complete System Workflow

The complete project workflow can be understood as a sequence of activities:

### Step 1 – Product Registration

The **farmer uploads agricultural produce** into the system and provides relevant product information and pricing.

**Farmer → Add Produce → Set Price**

### Step 2 – Product Management

The product information is stored in the **MySQL database** and becomes available within the system.

**Product Details → Java Application → MySQL Database**

### Step 3 – Quality Management

The **admin performs quality checks** and coordinates the next stages of the supply chain.

**Product → Admin → Quality Check**

### Step 4 – Consumer Ordering

The consumer browses the available products and selects the required produce.

**Consumer → Browse Products → Select Product → Place Order**

### Step 5 – Payment

The consumer proceeds with payment processing for the selected order.

**Order → Payment Processing → Order Confirmation**

### Step 6 – Logistics

The order information is coordinated with the distributor.

The distributor manages the required **transportation and storage activities**.

**Confirmed Order → Distributor → Transportation/Storage**

### Step 7 – Product Delivery

The product moves through the distribution process toward the consumer.

**Farmer → Distributor → Consumer**

### Step 8 – Feedback

After receiving the product, the consumer can provide a rating and feedback.

**Consumer → Rating/Feedback → System**

---

# Overall Architecture

The project can be represented as:

**Farmer**
↓
**Product Listing & Pricing**
↓
**Admin / Quality Check**
↓
**MySQL Database**
↓
**Consumer Order**
↓
**Distributor / Logistics**
↓
**Product Delivery**
↓
**Consumer Feedback**

The Java application acts as the main application layer connecting these different operations, while MySQL stores the system's data and SQL is used for database operations.

---

# Key Functionalities

* Farmer product registration
* Product and pricing management
* Customer order management
* Quality checking
* Transportation management
* Storage coordination
* Payment processing
* Product delivery workflow
* Consumer ratings and feedback
* Centralized database management

---

# Technology Stack

| Technology | Purpose                                  |
| ---------- | ---------------------------------------- |
| **Java**   | Application logic and system development |
| **MySQL**  | Data storage and management              |
| **SQL**    | Database queries and operations          |

The project presentation specifically identifies Java for application logic, SQL for database queries, and MySQL for data storage.

---

# Project Focus

The project focuses on creating a **centralized digital supply-chain system** where each participant has a defined role.

Instead of treating farmers, consumers, administration, and distribution as separate activities, the system connects them through a common workflow:

**Produce → Quality Check → Order → Logistics → Delivery → Feedback**

This provides a structured approach to managing agricultural products from their initial listing by the farmer through the consumer's purchase and feedback.
