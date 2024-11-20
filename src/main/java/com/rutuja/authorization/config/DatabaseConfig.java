package com.rutuja.authorization.config;

import com.rutuja.authorization.exceptions.MySqlConnectionClosedException;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.util.retry.Retry;
import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, "localhost")
                .option(PORT, 3306)
                .option(USER, "root")
                .option(PASSWORD, "root")
                .option(DATABASE, "practice")
                .build());
    }

    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();
    }

    @Bean
    public Retry retrySpec() {
        return Retry.backoff(5, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(10))
                .jitter(0.5)
                .filter(throwable -> throwable instanceof MySqlConnectionClosedException)
                .doAfterRetry(signal ->
                        System.out.println("Retrying connect attempt #" + signal.totalRetries()));
    }
}