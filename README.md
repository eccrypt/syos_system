# SYOS System

A comprehensive Java-based supermarket management system built with JSP and Servlets, featuring a complete web interface for inventory management, billing, reporting, and user authentication.

## Architecture

This application follows the traditional **JSP-Servlet architecture** with proper separation of concerns:

- **Presentation Layer**: JSP pages handle the user interface and form rendering
- **Controller Layer**: Servlets process requests, handle business logic, and manage data flow
- **Business Logic Layer**: Service classes implement core business rules
- **Data Access Layer**: Repository classes handle database operations
- **Domain Layer**: Model classes represent business entities

## Prerequisites

- Java 17
- Maven 3.x
- Apache Tomcat 9+ (servlet container)
- PostgreSQL database

## Project Structure

```
syos_system/
├── src/
│   ├── main/
│   │   ├── java/com/syos/
│   │   │   ├── application/           # Application Services Layer
│   │   │   │   ├── command/          # Command Pattern Implementation
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── factory/          # Factory Pattern
│   │   │   │   ├── service/          # Business Logic Services
│   │   │   │   └── strategy/         # Strategy Pattern
│   │   │   ├── domain/               # Domain Model Layer
│   │   │   │   ├── enums/            # Domain Enumerations
│   │   │   │   ├── exception/        # Custom Exceptions
│   │   │   │   ├── model/            # Domain Entities
│   │   │   │   └── observer/         # Observer Pattern
│   │   │   ├── infrastructure/       # Infrastructure Layer
│   │   │   │   ├── config/           # Configuration Classes
│   │   │   │   ├── db/               # Database Management
│   │   │   │   ├── repository/       # Data Access Layer
│   │   │   │   ├── singleton/        # Singleton Pattern
│   │   │   │   └── util/             # Utility Classes
│   │   │   └── presentation/         # Presentation Layer
│   │   │       ├── AuthServlet.java          # Authentication Controller
│   │   │       ├── BillingServlet.java       # Billing Controller
│   │   │       ├── InventoryServlet.java     # Inventory Controller
│   │   │       ├── ProductWebServlet.java    # Product Web Controller
│   │   │       ├── ReportServlet.java        # Reports Controller
│   │   │       └── SyosSystem.java           # Main Application Class
│   │   ├── resources/
│   │   │   └── application.properties        # Database Configuration
│   │   └── webapp/                           # Web Application Root
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                   # Servlet Configuration
│   │       │   └── jsp/                      # Protected JSP Templates
│   │       │       ├── admin/                # Admin Templates
│   │       │       │   ├── billing/          # Billing Pages
│   │       │       │   ├── discounts/        # Discount Management
│   │       │       │   ├── inventory/        # Inventory Management
│   │       │       │   ├── products/         # Product Management
│   │       │       │   ├── reports/          # Report Pages
│   │       │       │   └── dashboard.jsp     # Admin Dashboard
│   │       │       ├── common/               # Shared Components
│   │       │       │   ├── header.jsp        # Page Header
│   │       │       │   └── footer.jsp        # Page Footer
│   │       │       └── error/                # Error Pages
│   │       │           └── 404.jsp           # 404 Error Page
│   │       ├── admin/                        # Public Admin Pages
│   │       │   ├── billing/                  # Billing Interface
│   │       │   │   ├── bills.jsp            # Bills Management
│   │       │   │   └── create-bill.jsp      # Create Bill Form
│   │       │   ├── discounts/                # Discount Management
│   │       │   │   └── list.jsp             # Discount List
│   │       │   ├── inventory/                # Inventory Management
│   │       │   │   ├── stocks.jsp           # Stock Overview
│   │       │   │   ├── receive-stock.jsp    # Receive Stock Form
│   │       │   │   └── move-stock.jsp       # Move Stock Form
│   │       │   ├── products/                 # Product Management
│   │       │   │   ├── list.jsp             # Product List
│   │       │   │   └── add.jsp              # Add Product Form
│   │       │   ├── reports/                  # Reports Interface
│   │       │   │   ├── daily-sales.jsp      # Daily Sales Report
│   │       │   │   ├── transactions.jsp     # Transaction Report
│   │       │   │   └── product-stock.jsp    # Stock Report
│   │       │   └── dashboard.jsp            # Admin Dashboard
│   │       ├── css/                         # Stylesheets
│   │       │   ├── bootstrap.min.css        # Bootstrap Framework
│   │       │   └── custom.css               # Custom Styles
│   │       ├── js/                          # JavaScript Files
│   │       │   └── custom.js                # Custom Scripts
│   │       ├── index.jsp                    # Home Page
│   │       ├── login.jsp                    # Login Page
│   │       └── register.jsp                 # Registration Page
│   └── test/                                # Test Classes
│       └── java/com/test/                   # Unit Tests
├── target/                                  # Build Output
├── pom.xml                                  # Maven Configuration
├── README.md                                # This File
└── README_JSP_DESIGN.md                     # JSP Design Documentation
```

## Building

```bash
mvn clean package
```

## Running the Application

### Option 1: Deploy to Tomcat
1. Build the project: `mvn clean package`
2. Copy the generated WAR file to Tomcat's `webapps` directory
3. Start Tomcat server
4. Access the application at `http://localhost:9090/syos_system/`

### Option 2: Run Standalone (Embedded Tomcat)
1. Build the project: `mvn clean compile`
2. Run: `mvn exec:java -Dexec.mainClass="com.syos.presentation.SyosSystem"`
3. Access the application at `http://localhost:9090/`

## Application URLs

### Public Pages
- `GET /` - Home page
- `GET /login.jsp` - User login
- `GET /register.jsp` - User registration

### Admin Interface (Requires Authentication)
- `GET /admin/dashboard.jsp` - Admin dashboard
- `GET /admin/products/list.jsp` - Product management
- `GET /admin/inventory/stocks.jsp` - Inventory management
- `GET /admin/billing/create-bill.jsp` - Create bills
- `GET /admin/billing/bills.jsp` - View bills
- `GET /admin/discounts/list.jsp` - Discount management
- `GET /admin/reports/daily-sales.jsp` - Daily sales reports
- `GET /admin/reports/transactions.jsp` - Transaction reports
- `GET /admin/reports/product-stock.jsp` - Stock reports

## API Endpoints

### Authentication (`/auth`, `/logout`)
- `POST /auth` - User authentication
- `POST /logout` - User logout

### Billing API (`/billing/*`)
- `POST /billing/create` - Create a new bill
- `GET /billing/bills` - Get all bills
- `GET /billing/bills/{serialNumber}` - Get bill details

### Inventory API (`/inventory/*`)
- `POST /inventory/add-product` - Add new product
- `POST /inventory/update-product` - Update product
- `POST /inventory/receive-stock` - Receive stock
- `POST /inventory/move-to-shelf` - Move stock to shelf
- `POST /inventory/discard-batch` - Discard stock batch
- `POST /inventory/create-discount` - Create discount
- `POST /inventory/assign-discount` - Assign discount to product
- `POST /inventory/unassign-discount` - Remove discount from product
- `GET /inventory/products` - Get all products
- `GET /inventory/stocks` - Get stock information
- `GET /inventory/inventory-stocks` - Get inventory stocks
- `GET /inventory/expiry-stocks` - Get expiring stocks
- `GET /inventory/expiring-batches` - Get expiring batches
- `GET /inventory/discounts` - Get all discounts
- `GET /inventory/products-with-discounts` - Get products with discounts

### Product Management (`/admin/products/*`)
- `GET /admin/products/list` - List products (web interface)
- `POST /admin/products/add` - Add product (web interface)
- `POST /admin/products/update` - Update product (web interface)
- `POST /admin/products/delete` - Delete product (web interface)

### Reports API (`/reports/*`)
- `GET /reports/daily-sales?date=YYYY-MM-DD` - Daily sales report
- `GET /reports/all-transactions` - All transactions report
- `GET /reports/product-stock` - Product stock report
- `GET /reports/analysis` - Analysis report

## Database Configuration

Configure PostgreSQL connection in `src/main/resources/application.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/syos_db
db.username=your_username
db.password=your_password
db.driver=org.postgresql.Driver
```

## Design Patterns Implemented

- **Command Pattern**: For inventory operations
- **Factory Pattern**: For bill item creation
- **Strategy Pattern**: For pricing and shelf management
- **Observer Pattern**: For stock alerts
- **Singleton Pattern**: For inventory manager
- **Repository Pattern**: For data access abstraction

## Technologies Used

- **Java 17**: Core programming language
- **Servlet API 4.0**: Web request handling
- **JSP 2.3**: Server-side templating
- **PostgreSQL**: Database management
- **Bootstrap 5**: Frontend styling
- **Maven**: Build management
- **Jackson**: JSON processing

## Security Features

- Session-based authentication
- Role-based access control (ADMIN, STAFF)
- Input validation and sanitization
- CSRF protection through proper form handling
- Secure password handling

## Project Compliance

- JRE 17 (updated from JRE 15)
- Java Servlet 4.0 specification
- JSP 2.3 specification
- Maven 3.x build system
