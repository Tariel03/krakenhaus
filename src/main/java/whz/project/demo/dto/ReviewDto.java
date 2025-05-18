package whz.project.demo.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class ReviewDto {
    @Column(columnDefinition = "TEXT")
    String comment;
    Short review;
    Long arzt_id;
    Long patient_id;

}
