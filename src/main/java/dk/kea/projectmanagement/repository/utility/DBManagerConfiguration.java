package dk.kea.projectmanagement.repository.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DBManagerConfiguration {

    @Bean
    @Profile("prod")
    public DBManager prodDBManager() {
        return new ProductionDBManager();
    }

    @Bean
    @Profile("test")
    public DBManager testDBManager() {
        return new TestDBManager();
    }
}
