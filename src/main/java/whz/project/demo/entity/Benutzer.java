package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import whz.project.demo.enums.Geschlecht;
import whz.project.demo.enums.Role;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "tb_benutzer")
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


}
