package pl.edu.agh.data_collection.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.data_collection.config.ContextPath;

@RestController
public class HealthController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(ContextPath.HEALTH_CHECK_PATH)
    public ResponseEntity<Object> checkLiveness(){
        logger.info("{}: {} ---> health check", HttpMethod.GET, ContextPath.HEALTH_CHECK_PATH);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
