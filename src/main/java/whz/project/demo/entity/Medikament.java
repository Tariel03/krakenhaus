package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_medikament")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Medikament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;
    String hersteller;
    String dosierung;


    @ManyToMany(mappedBy = "medikamentList")
    List<Rezept> rezeptList;

}
