package pl.edu.agh.data_collection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.data_collection.config.ContextPath;

@RestController
public class HealthController {

    @GetMapping(ContextPath.HEALTH_CHECK_PATH)
    public ResponseEntity<Object> checkLiveness(){
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
