package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import whz.project.demo.enums.Geschlecht;
import whz.project.demo.enums.Role;

import java.time.LocalDate;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_benutzer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public abstract class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String vorname;
    private String nachname;
    private String login;
    private String password;
    private LocalDate geburtsdatum;
    private String titel;

    @Enumerated(EnumType.STRING)
    private Geschlecht geschlecht;

    private String telefonnummer;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String mainImage;

    @Column(name = "is_active", columnDefinition = "bit default 1")
    private boolean isActive = true;



    @ElementCollection
    @CollectionTable(name = "benutzer_gallery", joinColumns = @JoinColumn(name = "benutzer_id"))
    @Column(name = "image_path")
    private List<String> gallery;
}
