package whz.project.demo.dto;

import lombok.Data;

@Data

public class CancelTerminRequestDto {
    private Long terminId;
    private String reason;
}
