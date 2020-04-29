package pl.edu.agh.data_collection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;
import pl.edu.agh.data_collection.persistence.repository.UserRepository;

@Configuration
@ConfigurationProperties(prefix = "default")
public class StartupConfig {

    private final PasswordEncoder encoder;
    private final UserRepository repository;
    @Value("${default.admin.username}")
    private String adminLogin;
    @Value("${default.admin.password}")
    private String adminPassword;

    public static final int MIN_LOGIN_LENGTH = 8;
    public static final int MAX_LOGIN_LENGTH = 255;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 255;

    @Autowired
    StartupConfig(PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @Bean
    CommandLineRunner init(){
        return args -> {
            if(!repository.findByLogin(adminLogin).isPresent()) {
                UserEntity admin = new UserEntity();
                admin.setLogin(adminLogin);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setAuthority(UserRole.ADMIN);

                repository.save(admin);
            }
        };
    }
}
