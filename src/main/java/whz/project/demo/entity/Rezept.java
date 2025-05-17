package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_rezept")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Rezept {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(columnDefinition = "TEXT")
    String beschreibung;
    LocalDateTime datum_uhrzeit;
    @ManyToOne
    @JoinColumn(name = "termin_id")
    Termin termin;

    @ManyToMany
    @JoinTable(
            name = "rezept_medikament",
            joinColumns = @JoinColumn(name = "rezept_id"),
            inverseJoinColumns = @JoinColumn(name = "medikament_id")
    )
    private List<Medikament> medikamentList;

}
