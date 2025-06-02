package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private Short review;

    private LocalDate datum;
    private LocalTime uhrzeit;

    @ManyToOne
    @JoinColumn(name = "arzt_id")
    private Arzt arzt;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @PrePersist
    protected void onCreate() {
        this.datum = LocalDate.now();
        this.uhrzeit = LocalTime.now();
    }
}

