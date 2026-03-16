package com.example.find_me_a_doc_backend;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class FindMeADocBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindMeADocBackendApplication.class, args);
    }

    // Forcing Flyway to Run ---
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        return flyway;
    }
}