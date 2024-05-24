package thermolearn.backend.api.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import thermolearn.backend.api.services.SecretsManagerService;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DatabaseConfig {

    @Autowired
    private SecretsManagerService secretsManagerService;

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(secretsManagerService.getSecretValue("DB_URL"));
        dataSource.setUsername(secretsManagerService.getSecretValue("DB_USERNAME"));
        dataSource.setPassword(secretsManagerService.getSecretValue("DB_PASSWORD"));
        return dataSource;
    }
}