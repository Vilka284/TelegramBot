package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "Participant")
@Table(name = "participant")
@Data
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    private String tag;

    @Basic
    private String name;

    @Basic
    @Column(name = "chat_id")
    private Long chatId;

    @Basic
    private String operation;
}
