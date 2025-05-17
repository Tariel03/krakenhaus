package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "tb_medikament")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Medikament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;
    String hersteller;


    @ManyToMany(mappedBy = "medikamentList")
    List<Rezept> rezeptList;

}
