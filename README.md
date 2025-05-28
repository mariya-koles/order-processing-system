# Order Processing System

A Spring Boot application demonstrating asynchronous order processing using ActiveMQ message queues with Dead Letter Queue (DLQ) implementation for robust error handling.

## Overview

This system provides a REST API for order placement that asynchronously processes orders through JMS queues. Failed orders are automatically routed to a Dead Letter Queue after retry attempts, ensuring no orders are lost and providing visibility into processing failures.

## Architecture

- **REST API**: Accepts order placement requests via HTTP
- **JMS Producer**: Sends orders to ActiveMQ queue for asynchronous processing
- **JMS Consumer**: Processes orders from the queue
- **Dead Letter Queue**: Captures failed orders after retry attempts
- **Retry Mechanism**: Automatically retries failed orders (3 attempts with 2-second delays)

## Key Features

- **Asynchronous Processing**: Orders are processed asynchronously via JMS queues
- **Error Handling**: Comprehensive error handling with DLQ implementation
- **Retry Logic**: Configurable retry mechanism for transient failures
- **Monitoring**: Detailed logging and error tracking
- **Validation**: Input validation for order data
- **Testing**: Comprehensive test suite with 100% pass rate

## Technology Stack

- **Java 17**
- **Spring Boot 3.4.5**
- **Apache ActiveMQ**
- **Spring JMS**
- **Maven**
- **JUnit 5 & Mockito** (Testing)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for ActiveMQ)

## Quick Start

### 1. Start ActiveMQ

```bash
docker run -d --name activemq-dlq \
  -p 61616:61616 -p 8161:8161 \
  -v $(pwd)/activemq-dlq-simple.xml:/opt/activemq/conf/activemq.xml \
  apache/activemq-classic:latest
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access ActiveMQ Web Console

Visit `http://localhost:8161/admin` (admin/admin) to monitor queues and messages.

## API Documentation

### Place Order

**Endpoint:** `POST /api/orders`

**Request Body:**
```json
{
  "orderId": "ORD-001",
  "customerName": "John Doe",
  "product": "Laptop",
  "quantity": 2,
  "price": 999.99
}
```

**Response:** `200 OK` with message "Order placed successfully!"

### Swagger UI

Access interactive API documentation at `http://localhost:8080/swagger-ui.html`

## Demo Scenarios

### 1. Normal Order Processing

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "DEMO-001",
    "customerName": "Demo Customer",
    "product": "Demo Product",
    "quantity": 1,
    "price": 99.99
  }'
```

**Expected Result:** Order processed successfully, visible in application logs.

### 2. DLQ Testing (Optional)

To test the DLQ functionality, temporarily enable forced failures in `MessageConsumer.java`:

```java
// Uncomment these lines in receiveOrder method:
// log.error("Forcing failure for order: {} - This will trigger DLQ after retries", order.getOrderId());
// throw new RuntimeException("Forced processing failure for DLQ test");
```

Send an order and observe:
- 3 retry attempts in logs
- Order appears in `orders.queue.DLQ` in ActiveMQ console
- DLQ listener processes the failed order

### 3. Monitoring

- **Application Logs**: Monitor console output for processing details
- **ActiveMQ Console**: View queue depths and message flow at `http://localhost:8161/admin`
- **Health Check**: `GET http://localhost:8080/actuator/health`

## Configuration

Key configuration properties in `application.properties`:

```properties
# ActiveMQ Configuration
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin

# JMS Configuration
spring.jms.listener.acknowledge-mode=client
spring.jms.listener.auto-startup=true

# Logging
logging.level.com.platform.ops=INFO
```

## Testing

Run the comprehensive test suite:

```bash
# All tests
mvn test

# Specific test categories
mvn test -Dtest=*ControllerTest
mvn test -Dtest=*IntegrationTest
```

**Test Coverage:**
- 41 tests total with 100% pass rate
- Unit tests for all components
- Integration tests for end-to-end flows
- Configuration validation tests

## Project Structure

```
src/
├── main/java/com/platform/ops/
│   ├── controller/     # REST API controllers
│   ├── service/        # Business logic services
│   ├── model/          # Data models
│   ├── jms/            # JMS message consumers
│   ├── config/         # Configuration classes
│   └── exception/      # Exception handlers
└── test/java/          # Comprehensive test suite
```

## Troubleshooting

### Common Issues

1. **ActiveMQ Connection Failed**
   - Ensure ActiveMQ is running on port 61616
   - Check Docker container status: `docker ps`

2. **Orders Not Processing**
   - Verify JMS listeners are active in application logs
   - Check ActiveMQ console for queue status

3. **DLQ Not Working**
   - Ensure ActiveMQ is configured with DLQ policy
   - Verify `activemq-dlq-simple.xml` configuration

### Logs

Monitor application logs for detailed processing information:
```bash
tail -f logs/app.log
```

## Development

### Building

```bash
mvn clean compile
```

### Running Tests

```bash
mvn clean test
```

### Packaging

```bash
mvn clean package
```

## License

This project is for demonstration purposes.

---

For detailed test documentation, see [Test Suite Documentation](README_TESTS.md). 