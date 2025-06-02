package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "arzt")
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


    @ManyToOne
    @JoinColumn(name = "arzt_id", nullable = false)
    private Arzt arzt;

}
