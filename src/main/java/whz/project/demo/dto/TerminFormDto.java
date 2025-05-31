package whz.project.demo.dto;

import lombok.Data;
import whz.project.demo.enums.TerminStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TerminFormDto {
    private LocalDate datum;
    private LocalTime uhrzeit;
    private Long arztId;
    private Long patientId; // допускается null
    private TerminStatus status;
    private String diagnose;
    private String notizen;
}
