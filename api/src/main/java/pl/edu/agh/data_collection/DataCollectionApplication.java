package pl.edu.agh.data_collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.edu.agh.data_collection.config.StartupConfig;

@SpringBootApplication
@EnableConfigurationProperties(StartupConfig.class)
public class DataCollectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataCollectionApplication.class, args);
    }

}
