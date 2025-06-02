package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "tb_fachrichtung")
@Entity
@Getter
@Setter
@ToString(exclude = "arztList")
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Fachrichtung {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;


    @ManyToMany(mappedBy = "fachrichtungList")
    private List<Arzt> arztList;



}
