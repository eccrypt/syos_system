# SYOS System

A Java-based supermarket management system converted from CLI to a web API using servlets.

## Prerequisites
- Java 17
- Maven 3.x
- Apache Tomcat 9+ (servlet container)

## Building
```bash
mvn clean package
```

## Running the Application

### Option 1: Deploy to Tomcat
1. Build the project: `mvn clean package`
2. Copy the generated WAR file (`target/syos-billing-system-0.0.1-SNAPSHOT.war`) to Tomcat's `webapps` directory
3. Start Tomcat server
4. Access the APIs at `http://localhost:8080/syos-billing-system-0.0.1-SNAPSHOT/api/...`

### Option 2: Run Standalone (Embedded Tomcat)
1. Build the project: `mvn clean compile`
2. Run: `mvn exec:java -Dexec.mainClass="com.syos.presentation.SyosSystem"`
3. Access the APIs at `http://localhost:8080/api/...`

## API Endpoints

### Billing API (`/api/billing/*`)
- `POST /api/billing/create` - Create a new bill
  - Body: `{"items": [{"productCode": "string", "quantity": int}], "cashTendered": double}`
  - Response: Bill details with serial number, total, change

### Inventory API (`/api/inventory/*`)
- `POST /api/inventory/add-product` - Add new product
- `POST /api/inventory/update-product` - Update product
- `POST /api/inventory/receive-stock` - Receive stock
- `POST /api/inventory/move-to-shelf` - Move stock to shelf
- `GET /api/inventory/products` - Get all products
- `GET /api/inventory/stocks` - Get stock information

### Online Store API (`/api/store/*`)
- `POST /api/store/register` - Register customer
- `POST /api/store/login` - Login customer
- `GET /api/store/products` - Browse products
- `GET /api/store/products/{code}` - Search product by code

### Reports API (`/api/reports/*`)
- `GET /api/reports/daily-sales?date=YYYY-MM-DD` - Daily sales report
- `GET /api/reports/all-transactions` - All transactions
- `GET /api/reports/product-stock` - Product stock report
- `GET /api/reports/analysis` - Analysis report

## Database
Uses PostgreSQL. Configure connection in `src/main/resources/application.properties`.

## Project Compliance
Must be set to JRE 17 (updated from JRE 15).
