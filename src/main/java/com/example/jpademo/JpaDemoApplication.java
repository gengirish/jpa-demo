package com.example.jpademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Main application class for the JPA Demo application.
 * This class serves as the entry point for the Spring Boot application.
 * Contains embedded configuration properties.
 */
@SpringBootApplication
@Configuration
public class JpaDemoApplication {

    public static void main(String[] args) {
        // Set system properties for embedded configuration
        System.setProperty("spring.datasource.url", "jdbc:h2:mem:productdb");
        System.setProperty("spring.datasource.driverClassName", "org.h2.Driver");
        System.setProperty("spring.datasource.username", "sa");
        System.setProperty("spring.datasource.password", "");
        
        System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        System.setProperty("spring.jpa.show-sql", "true");
        System.setProperty("spring.jpa.properties.hibernate.format_sql", "true");
        
        System.setProperty("spring.h2.console.enabled", "true");
        System.setProperty("spring.h2.console.path", "/h2-console");
        
        System.setProperty("logging.level.org.hibernate.SQL", "DEBUG");
        System.setProperty("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", "TRACE");
        
        SpringApplication.run(JpaDemoApplication.class, args);
    }
}
