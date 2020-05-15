package pl.edu.agh.data_collection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.data_collection.utils.JwtDecoder;
import pl.edu.agh.data_collection.utils.TimestampParser;
import pl.edu.agh.data_collection.utils.UsernameParser;


@Configuration
public class ParserBeans {

    @Bean
    public JwtDecoder getJwtDecoder() {
        return new JwtDecoder();
    }

    @Bean
    public UsernameParser getBearerParser(){
        return new UsernameParser(getJwtDecoder());
    }

    @Bean
    public TimestampParser getTimestampParser(){
        return new TimestampParser();
    }
}
