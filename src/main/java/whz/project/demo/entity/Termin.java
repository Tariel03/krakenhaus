package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import whz.project.demo.enums.TerminStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Long id;

    @Column(nullable = false)
    private LocalDate datum;

    @Column(nullable = false)
    private LocalTime uhrzeit;

    private String diagnose;

    @Column(columnDefinition = "TEXT")
    private String notizen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TerminStatus status; // NEU

    @Column(nullable = false)
    private LocalDateTime erstelltAm;

    @ManyToOne
    @JoinColumn(name = "arzt_id")
    private Benutzer arzt;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Benutzer patient;

    @Transient
    private String cssClass;

    @PrePersist
    public void prePersist() {
        this.erstelltAm = LocalDateTime.now();
        if (this.status == null) {
            this.status = TerminStatus.FREI;
        }
    }
}
