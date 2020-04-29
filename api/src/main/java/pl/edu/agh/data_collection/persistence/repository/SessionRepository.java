package pl.edu.agh.data_collection.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.data_collection.persistence.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
}
