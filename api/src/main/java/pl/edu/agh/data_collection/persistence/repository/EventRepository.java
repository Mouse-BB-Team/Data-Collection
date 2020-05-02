package pl.edu.agh.data_collection.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.data_collection.persistence.entity.EventEntity;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    EventEntity findByName(String name);
    @Query(value = "select name from dc.events", nativeQuery = true)
    List<String> getAllNames();
}
