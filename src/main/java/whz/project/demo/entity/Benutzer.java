package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import whz.project.demo.enums.Geschlecht;
import whz.project.demo.enums.Role;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Table(name = "tb_benutzer")
@NoArgsConstructor
@AllArgsConstructor
public class Benutzer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String vorname;
    String nachname;
    String login;
    String password;
    LocalDate geburtsdatum;
    @Enumerated(EnumType.STRING)
    Geschlecht geschlecht;
    String telefonnummer;
    String email;
    String address;
    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToMany(mappedBy = "benutzerList")
    List<Fachrictung> fachrictungList;

    @Lob
    private String beschreibung;

    private String mainImage;


    @ManyToMany
    @JoinTable(
            name = "arzt_leistungen",
            joinColumns = @JoinColumn(name = "arzt_id"),
            inverseJoinColumns = @JoinColumn(name = "leistung_id")
    )
    private List<Leistungen> leistungenList;
}
