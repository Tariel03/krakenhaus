package whz.project.demo.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class FachrichtungDto {
    String name;
    @Column(columnDefinition = "TEXT")
    String beschreibung;
}
