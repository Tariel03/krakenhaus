package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "arzte")
@EqualsAndHashCode
@Builder
@Table(name = "tb_leistungen")
@NoArgsConstructor
@AllArgsConstructor
public class Leistungen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(length = 300)
    private String beschreibung;

    @ManyToMany(mappedBy = "leistungenList")
    private List<Benutzer> arzte;
}
