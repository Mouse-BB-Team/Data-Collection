package pl.edu.agh.data_collection.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.data_collection.persistence.entity.EventEntity;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findByName(String name);
}
