package whz.project.demo.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MedikamentDto {
    private Long id;
    private Long rezeptId;
    private String name;
    private String dosierung;
    private String beschreibung;
    private String hersteller;
}