package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(columnDefinition = "TEXT")
    String comment;
    Short review;
    LocalDate datum;
    LocalTime uhrzeit;
    @JoinColumn(name = "arzt_id")
    @ManyToOne
    Benutzer arzt;
    @JoinColumn(name = "patient_id")
    @ManyToOne
    Benutzer patient;
}
