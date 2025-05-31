package whz.project.demo.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.TerminRepository;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerminService {
    private final TerminRepository terminRepository;
    private final BenutzerRepository benutzerRepository;
    private final BenutzerService benutzerService;

    public List<Termin> findAllByArzt(Benutzer arzt){
        return terminRepository.findByArzt(arzt);
    }

    public void generateDailyTermine(Benutzer arzt, LocalDate date) {
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 30);

        while (start.isBefore(end)) {
            Termin termin = Termin.builder()
                    .arzt(arzt)
                    .datum(date)
                    .uhrzeit(start)
                    .status(TerminStatus.FREI)
                    .build();

            terminRepository.save(termin);
            start = start.plusMinutes(30);
        }
    }

    public void bookTermin(Long terminId, Long patientId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin nicht gefunden"));

        if (termin.getStatus() != TerminStatus.FREI) {
            throw new IllegalStateException("Termin ist bereits belegt oder nicht verfügbar.");
        }

        Benutzer patient = benutzerRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient nicht gefunden"));

        termin.setStatus(TerminStatus.GEBUCHT);
        termin.setPatient(patient);
        terminRepository.save(termin);
    }

    public List<Termin> findeAlleFuerArzt(Benutzer arzt) {
        return terminRepository.findByArztId(arzt.getId());
    }


    public void statusAktualisieren(Long terminId, TerminStatus status, Benutzer arzt) throws AccessDeniedException {
        Termin termin = findeMitZugriff(terminId, arzt);
        termin.setStatus(status);
        terminRepository.save(termin);
    }

    public Termin findeMitZugriff(Long terminId, Benutzer arzt) throws AccessDeniedException {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));

        System.out.println("Eingeloggter Benutzer: " + (arzt != null ? arzt.getId() : "null"));
        System.out.println("Termin-Arzt: " + (termin.getArzt() != null ? termin.getArzt().getId() : "null"));

        if (termin.getArzt() == null || arzt == null || !termin.getArzt().getId().equals(arzt.getId())) {
            throw new AccessDeniedException("Kein Zugriff auf diesen Termin");
        }

        return termin;
    }

    public Termin speichern(Termin termin) {
        return terminRepository.save(termin);
    }

}



