package whz.project.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'FREI'")
    private TerminStatus status;

    @Column(nullable = false, columnDefinition = "DATETIME2(6) DEFAULT GETDATE()")
    private LocalDateTime erstelltAm;

    @ManyToOne
    @JoinColumn(name = "arzt_id")
    @JsonBackReference(value = "arzt-termin")
    private Arzt arzt;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

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
