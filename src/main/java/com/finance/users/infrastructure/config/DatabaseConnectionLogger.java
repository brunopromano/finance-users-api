package com.finance.users.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class DatabaseConnectionLogger {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionLogger.class);

    private final DataSource dataSource;

    public DatabaseConnectionLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("Database connection established successfully");
            logger.info("Database URL: {}", metaData.getURL());
            logger.info("Database product: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            logger.info("Driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
        } catch (SQLException e) {
            logger.error("Failed to connect to the database: {}", e.getMessage(), e);
        }
    }
}
