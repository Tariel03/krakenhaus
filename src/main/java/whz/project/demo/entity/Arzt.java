package whz.project.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import whz.project.demo.enums.Geschlecht;
import whz.project.demo.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_arzt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PrimaryKeyJoinColumn(name = "id")
public class Arzt extends Benutzer {


    @Lob
    private String beschreibung;

    @ManyToMany
    @JoinTable(
            name = "arzt_fachrichtung",
            joinColumns = @JoinColumn(name = "arzt_id"),
            inverseJoinColumns = @JoinColumn(name = "fachrichtung_id")
    )
    private List<Fachrichtung> fachrichtungList;

    @OneToMany(mappedBy = "arzt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Leistungen> leistungen = new ArrayList<>();

}
