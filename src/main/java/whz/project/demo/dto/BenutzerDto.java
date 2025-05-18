package whz.project.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BenutzerDto {
    private Long id;
    private String vorname;
    private String nachname;
    private List<String> fachrichtungen;
    private Double averageReview;
}