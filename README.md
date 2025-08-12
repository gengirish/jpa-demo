# JPA Repository Demo

This project demonstrates the use of Spring Data JPA with custom queries. It includes a JPA entity (Product) and a corresponding Spring Data JPA repository interface with both standard CRUD methods and custom query methods using the @Query annotation.

## Project Structure

```
jpa-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── jpademo/
│   │   │               ├── JpaDemoApplication.java
│   │   │               ├── Product.java
│   │   │               └── ProductRepository.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── jpademo/
│       │               └── ProductRepositoryTest.java
│       └── resources/
│           └── application-test.properties
└── pom.xml
```

## Features

- **Product Entity**: A JPA entity with fields for id, name, description, price, category, stockQuantity, and availability.
- **ProductRepository Interface**: Extends JpaRepository to inherit standard CRUD operations.
- **Custom Query Methods**: Implements various custom query methods using @Query annotation with JPQL and native SQL.
- **Comprehensive Test Suite**: Tests for all repository methods, including edge cases and boundary tests.

## Custom Query Examples

The repository includes several custom query methods:

1. Finding products within a price range and specific category
2. Finding available products with stock below a threshold in a specific category
3. Finding top-selling products in a category
4. Using native SQL to find products with name matching a pattern
5. Finding the most expensive product in a category
6. Calculating average price by category
7. Counting products by category and price range

## Running the Project

### Prerequisites

- Java 17 or higher
- Maven

### Build and Run

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

### Running Tests

Run the tests with:

```
mvn test
```

## H2 Console

The application includes the H2 Console for database inspection:

1. Start the application
2. Navigate to http://localhost:8080/h2-console
3. Use the JDBC URL, username, and password from application.properties

## Technologies Used

- Spring Boot 3.1.0
- Spring Data JPA
- H2 Database
- JUnit 5
- Maven
