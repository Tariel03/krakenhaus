package whz.project.demo.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MedikamentDto {
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;
    String hersteller;
    String dosierung;

}
