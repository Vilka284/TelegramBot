package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity(name = "Queue")
@Table(name = "queue")
@Data
@NoArgsConstructor
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    private Date enter_date;

    @Basic
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Participant participant;
}