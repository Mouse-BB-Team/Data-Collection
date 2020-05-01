package pl.edu.agh.data_collection.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);
    @Query(value = "select id from dc.users where login = :login", nativeQuery = true)
    Optional<Long> getIdByLogin(@Param("login") String login);
}
