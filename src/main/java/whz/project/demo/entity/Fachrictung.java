package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "tb_fachrichtung")
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Fachrictung {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;


    @ManyToMany
            @JoinTable(name = "arzt_fachrichtung", joinColumns = @JoinColumn(name = "arzt_id"),
            inverseJoinColumns = @JoinColumn(name = "fachrictung_id"))
    List<Benutzer> benutzerList;

}
