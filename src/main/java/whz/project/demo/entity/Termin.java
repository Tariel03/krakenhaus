package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import whz.project.demo.enums.Geschlecht;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_termin")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Termin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    boolean frei;
    LocalDate datum;
    LocalTime uhrzeit;
    String diagnose;
    @Column(columnDefinition = "TEXT")
    String notizen;
    @JoinColumn(name = "arzt_id")
    @ManyToOne
    Benutzer arzt;
    @JoinColumn(name = "patient_id")
    @ManyToOne
    Benutzer patient;

}
