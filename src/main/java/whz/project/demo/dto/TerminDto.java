package whz.project.demo.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import whz.project.demo.entity.Benutzer;

import java.time.LocalTime;

public class TerminDto {
    LocalTime uhrzeit;
    String diagnose;
    String notizen;
    Long arzt_id;
    Long patient_id;

}
