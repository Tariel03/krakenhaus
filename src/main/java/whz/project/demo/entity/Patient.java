package whz.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PrimaryKeyJoinColumn(name = "id")
public class Patient extends Benutzer {

    @Column(name = "krankenkasse")
    private String krankenkasse;

    @Column(name = "versichertennummer")
    private String versichertennummer;


}
