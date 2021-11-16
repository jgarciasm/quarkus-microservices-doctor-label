package com.cocus.doctor.label;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer DATABASE = new PostgreSQLContainer<>("postgres:11.2")
        .withDatabaseName("labels_database")
        .withUsername("label")
        .withPassword("label")
        .withExposedPorts(5432);

    @Override
    public Map<String, String> start() {
        DATABASE.start();
        return Collections.singletonMap("quarkus.datasource.jdbc.url", DATABASE.getJdbcUrl());
    }

    @Override
    public void stop() {
        DATABASE.stop();
    }
}
