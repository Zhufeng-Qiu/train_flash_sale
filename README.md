# Train Flash Sale Platform

A high-concurrency train ticket booking system, featuring flash sale capabilities and microservices architecture.

## Project Overview

This project is a personal practice project based on core ticketing functionality, incorporating microservices and high-concurrency technologies to implement flash sale ticket purchasing. The system handles scenarios like 100,000 people competing for 1,000 tickets using advanced distributed technologies.

### Live Demo
- **User Portal**: [train-flash-sale.zhufqiu.com](https://train-flash-sale.zhufqiu.com)
- **Admin Panel**: [tfs-admin.zhufqiu.com](https://tfs-admin.zhufqiu.com)

## System Architecture

The system is built using microservices architecture, split into multiple independent service units:

- **Gateway Module**: API gateway for routing and authentication
- **Member Module**: User registration, login, and passenger management
- **Business Module**: Core ticketing functionality (ticket query, seat selection, ordering)
- **Batch Module**: Scheduled tasks for generating daily train schedules
- **Admin Module**: Backend management system for system administrators

## High-Concurrency Technologies

The ticket purchasing process implements multiple layers of high-concurrency optimization:

### Traffic Control & Security
- **CDN**: Improves user page access speed
- **Two-Layer Captcha System**: 
  - Frontend captcha to weaken instantaneous peaks
  - Backend captcha to prevent bot attacks
- **Rate Limiting**: Reduces unnecessary requests with quick failure responses
- **Token Distribution**: Controls ticket grabbing volume and prevents bot brushing

### Performance & Scalability
- **Distributed Caching**: High-performance ticket availability queries with cache breakdown/penetration/avalanche protection
- **Distributed Locks**: Prevents overselling (ensures only 1,000 tickets sold when 2,000 people compete for 1,000 tickets)
- **Asynchronous Peak Shaving + Queuing**: Solves throughput issues with immediate user feedback
- **Distributed Transactions**: Ensures eventual data consistency

## Technology Stack

### Frontend
- **Vue 3** + **Vue CLI 5**
- **Ant Design Vue** (UI components)
- **Vue Router** (routing)
- **Vuex** (state management)

### Backend
- **JDK 17**
- **Spring Boot 3**
- **Spring Cloud Alibaba**:
  - **Nacos** (service discovery & configuration)
  - **Seata** (distributed transactions)
  - **Sentinel** (circuit breaker & flow control)
  - **Feign** (service communication)
  - **Gateway** (API gateway)

### Data & Messaging
- **MySQL** (primary database)
- **Redis** (caching & session storage)
- **RocketMQ** (message queue)
- **MyBatis** (ORM)

### DevOps & Tools
- **Quartz** (job scheduling)
- **FreeMarker** (code generation)
- **JWT** (authentication)
- **Docker** (containerization)

## Database Structure

The project contains **15 business tables** covering:
- Train schedules and routes
- Station and carriage information
- Seat management
- User and passenger data
- Order and payment processing
- System configuration

## Features

### User Portal Features
- **User Authentication**: Registration, login with JWT
- **Passenger Management**: Add/edit passenger information
- **Ticket Search**: Query available tickets with real-time updates
- **Seat Selection**: Interactive seat map with availability
- **Order Management**: View purchase history and ticket details
- **Flash Sale**: High-concurrency ticket purchasing

### Admin Panel Features
- **Basic Data Management**: Train numbers, stations, carriages, seats
- **Business Data Management**: Daily schedules, ticket inventory, tokens
- **Member Management**: User accounts, passengers, issued tickets
- **Batch Job Management**: Create, delete, pause, restart scheduled tasks

## Getting Started

### Prerequisites
- Node.js 16+ 
- npm or yarn
- Backend services running (see main project README)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd web
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   ```bash
   # Update API endpoints in src/config/api.js
   # Configure backend service URLs
   ```

4. **Start development server**
   ```bash
   npm run serve
   ```

5. **Access the application**
   - Open [http://localhost:8080](http://localhost:8080) in your browser

### Build for Production

```bash
# Build for production
npm run build
```

## Project Structure

```
train-flash-sale/
├── Frontend Modules
│   ├── web/                           # User Portal (Vue 3)
│   │   ├── public/                    # Static assets
│   │   ├── src/
│   │   │   ├── assets/               # Images, styles, etc.
│   │   │   ├── components/           # Reusable Vue components
│   │   │   ├── views/               # Page components
│   │   │   │   ├── main/            # Main application pages
│   │   │   │   └── login/           # Authentication pages
│   │   │   ├── router/              # Vue Router configuration
│   │   │   ├── store/               # Vuex state management
│   │   │   └── main.js              # Application entry point
│   │   ├── package.json              # Dependencies and scripts
│   │   └── vue.config.js             # Vue CLI configuration
│   │
│   └── admin/                         # Admin Panel (Vue 3)
│       ├── public/                    # Static assets & images
│       ├── src/
│       │   ├── assets/               # Images, styles, enums
│       │   ├── components/           # Admin-specific components
│       │   │   ├── station-select.vue
│       │   │   ├── the-header.vue
│       │   │   ├── the-sider.vue
│       │   │   └── train-select.vue
│       │   ├── views/                # Admin pages
│       │   │   ├── main/
│       │   │   │   ├── base/         # Basic data management
│       │   │   │   │   ├── station.vue
│       │   │   │   │   ├── train.vue
│       │   │   │   │   ├── train-carriage.vue
│       │   │   │   │   ├── train-seat.vue
│       │   │   │   │   └── train-station.vue
│       │   │   │   ├── business/     # Business data management
│       │   │   │   │   ├── daily-train.vue
│       │   │   │   │   ├── daily-train-carriage.vue
│       │   │   │   │   ├── daily-train-seat.vue
│       │   │   │   │   ├── daily-train-station.vue
│       │   │   │   │   ├── daily-train-ticket.vue
│       │   │   │   │   ├── confirm-order.vue
│       │   │   │   │   └── sk-token.vue
│       │   │   │   ├── member/       # Member management
│       │   │   │   │   └── ticket.vue
│       │   │   │   ├── batch/        # Batch job management
│       │   │   │   │   └── job.vue
│       │   │   │   ├── about.vue
│       │   │   │   └── welcome.vue
│       │   │   └── main.vue
│       │   ├── router/               # Vue Router configuration
│       │   ├── store/                # Vuex state management
│       │   └── main.js               # Application entry point
│       ├── package.json              # Dependencies and scripts
│       └── vue.config.js             # Vue CLI configuration
│
├── Backend Modules (Spring Boot)
│   ├── gateway/                       # API Gateway Module
│   │   ├── src/main/java/
│   │   │   └── com/zephyr/train/gateway/
│   │   │       ├── config/           # Gateway configuration
│   │   │       ├── controller/       # Gateway controllers
│   │   │       └── filter/           # Request filters
│   │   ├── src/main/resources/
│   │   │   ├── application.properties
│   │   │   └── bootstrap.properties
│   │   └── pom.xml                   # Maven dependencies
│   │
│   ├── member/                        # Member Service Module
│   │   ├── src/main/java/
│   │   │   └── com/zephyr/train/member/
│   │   │       ├── config/           # Member configuration
│   │   │       ├── controller/       # Member controllers
│   │   │       │   ├── admin/        # Admin controllers
│   │   │       │   ├── feign/        # Feign clients
│   │   │       │   ├── MemberController.java
│   │   │       │   ├── PassengerController.java
│   │   │       │   └── TicketController.java
│   │   │       ├── domain/           # Entity classes
│   │   │       │   ├── Member.java
│   │   │       │   ├── Passenger.java
│   │   │       │   └── Ticket.java
│   │   │       ├── enums/            # Enumerations
│   │   │       ├── mapper/           # MyBatis mappers
│   │   │       ├── req/              # Request DTOs
│   │   │       ├── resp/             # Response DTOs
│   │   │       └── service/          # Business services
│   │   ├── src/main/resources/
│   │   │   ├── mapper/               # MyBatis XML files
│   │   │   ├── application.properties
│   │   │   └── bootstrap.properties
│   │   └── pom.xml
│   │
│   ├── business/                      # Business Service Module (Core)
│   │   ├── src/main/java/
│   │   │   └── com/zephyr/train/business/
│   │   │       ├── config/           # Business configuration
│   │   │       │   ├── BusinessApplication.java
│   │   │       │   ├── KaptchaConfig.java
│   │   │       │   ├── RocketMQConfig.java
│   │   │       │   └── SpringMvcConfig.java
│   │   │       ├── controller/       # Business controllers
│   │   │       │   ├── admin/        # Admin controllers
│   │   │       │   ├── ConfirmOrderController.java
│   │   │       │   ├── DailyTrainTicketController.java
│   │   │       │   ├── KaptchaController.java
│   │   │       │   ├── SeatSellController.java
│   │   │       │   └── TrainController.java
│   │   │       ├── domain/           # Entity classes
│   │   │       │   ├── ConfirmOrder.java
│   │   │       │   ├── DailyTrain.java
│   │   │       │   ├── DailyTrainTicket.java
│   │   │       │   ├── Station.java
│   │   │       │   ├── Train.java
│   │   │       │   └── SkToken.java
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       ├── enums/            # Business enumerations
│   │   │       ├── feign/            # Feign clients
│   │   │       ├── mapper/           # MyBatis mappers
│   │   │       │   └── cust/         # Custom mappers
│   │   │       ├── mq/               # Message Queue consumers
│   │   │       ├── req/              # Request DTOs
│   │   │       ├── resp/             # Response DTOs
│   │   │       └── service/          # Business services
│   │   ├── src/main/resources/
│   │   │   ├── mapper/               # MyBatis XML files
│   │   │   │   └── cust/            # Custom mapper XMLs
│   │   │   ├── application.properties
│   │   │   └── bootstrap.properties
│   │   └── pom.xml
│   │
│   ├── batch/                        # Batch Job Module
│   │   ├── src/main/java/
│   │   │   └── com/zephyr/train/batch/
│   │   │       ├── config/           # Batch configuration
│   │   │       ├── controller/       # Batch controllers
│   │   │       ├── domain/           # Batch entities
│   │   │       ├── enums/            # Batch enumerations
│   │   │       ├── mapper/           # Batch mappers
│   │   │       ├── req/              # Batch request DTOs
│   │   │       ├── resp/             # Batch response DTOs
│   │   │       └── service/          # Batch services
│   │   ├── src/main/resources/
│   │   │   ├── mapper/               # Batch mapper XMLs
│   │   │   ├── application.properties
│   │   │   └── bootstrap.properties
│   │   └── pom.xml
│   │
│   ├── common/                       # Common Module
│   │   ├── src/main/java/
│   │   │   └── com/zephyr/train/common/
│   │   │       ├── exception/        # Common exceptions
│   │   │       ├── resp/             # Common responses
│   │   │       └── util/              # Common utilities
│   │   └── pom.xml
│   │
│   └── generator/                     # Code Generator Module
│       ├── src/main/java/
│       │   └── com/zephyr/train/generator/
│       │       ├── controller/       # Generator controllers
│       │       ├── domain/           # Generator entities
│       │       ├── mapper/           # Generator mappers
│       │       ├── req/              # Generator requests
│       │       └── service/          # Generator services
│       ├── src/main/resources/
│       │   ├── template/             # FreeMarker templates
│       │   └── application.properties
│       └── pom.xml
│
├── Configuration & Deployment
│   ├── deploy/                        # Deployment scripts
│   │   ├── deploy-batch.sh
│   │   ├── deploy-business.sh
│   │   ├── deploy-gateway.sh
│   │   └── deploy-member.sh
│   │
│   ├── sql/                          # Database scripts
│   │   ├── batch.sql
│   │   ├── business.sql
│   │   ├── member.sql
│   │   └── seata.sql
│   │
│   ├── http/                         # API testing files
│   │   ├── business-*.http
│   │   ├── member-*.http
│   │   └── batch-*.http
│   │
│   ├── log/                          # Application logs
│   │   ├── batch/
│   │   ├── business/
│   │   ├── gateway/
│   │   └── member/
│   │
│   ├── pom.xml                       # Root Maven configuration
│   ├── mvnw                          # Maven wrapper
│   └── README.md                     # Project documentation
```

### Module Responsibilities

#### Frontend Modules
- **web/**: User-facing portal for ticket booking, passenger management, and order viewing
- **admin/**: Administrative interface for system management, data maintenance, and monitoring

#### Backend Modules
- **gateway/**: API gateway handling routing, authentication, and cross-cutting concerns
- **member/**: User management, authentication, passenger management, and ticket viewing
- **business/**: Core ticketing logic, seat management, order processing, and flash sale functionality
- **batch/**: Scheduled tasks for generating daily train schedules and data maintenance
- **common/**: Shared utilities, exceptions, and common response structures
- **generator/**: Code generation tool for rapid CRUD development using FreeMarker templates

## Development Features

### Code Generation
- **Custom Code Generator**: Built with FreeMarker for rapid CRUD operation generation
- **One-Click Generation**: Automatically generates both frontend and backend code for single tables

### Performance Optimizations
- **Redis Caching**: Optimized ticket query performance
- **CDN Integration**: Improved page load times
- **Lazy Loading**: Component and route-based code splitting

