package pl.edu.agh.data_collection.persistence.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sessions")
public class SessionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "x_coordinate")
    private Integer x;
    @Column(name = "y_coordinate")
    private Integer y;
    @Column(name = "event_id")
    private Long event;
    @Column(name = "event_time")
    private LocalDateTime eventTime;
}
