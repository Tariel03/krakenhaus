package whz.project.demo.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.repos.TerminRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerminService {
    private final TerminRepository terminRepository;
    private final PatientService patientService;

    public List<Termin> findAllByArzt(Arzt arzt){
        return terminRepository.findByArzt(arzt);
    }


    public void bookTermin(Long terminId, Long patientId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin nicht gefunden"));

        if (termin.getStatus() != TerminStatus.FREI) {
            throw new IllegalStateException("Termin ist bereits belegt oder nicht verfügbar.");
        }

        Patient patient = patientService.findById(patientId);

        termin.setStatus(TerminStatus.GEBUCHT);
        termin.setPatient(patient);
        terminRepository.save(termin);
    }

    public List<Termin> findeAlleFuerArzt(Arzt arzt) {
        return terminRepository.findByArztId(arzt.getId());
    }


    public void statusAktualisieren(Long terminId, TerminStatus status, Long arztId) throws AccessDeniedException {
        Termin termin = findeMitZugriff(terminId, arztId);
        termin.setStatus(status);
        terminRepository.save(termin);
    }

    public Termin findeMitZugriff(Long terminId, Long arztId) throws AccessDeniedException {
        if (arztId == null) {
            throw new AccessDeniedException("Nicht autorisiert");
        }

        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));

        if (termin.getArzt() == null || !termin.getArzt().getId().equals(arztId)) {
            throw new AccessDeniedException("Kein Zugriff auf diesen Termin");
        }

        return termin;
    }


    public Termin speichern(Termin termin) {
        return terminRepository.save(termin);
    }


    public void storniereTermin(Long terminId, Long patientId) throws AccessDeniedException {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));

        if (termin.getPatient() == null || !termin.getPatient().getId().equals(patientId)) {
            throw new AccessDeniedException("Kein Zugriff zum Stornieren dieses Termins");
        }

        termin.setPatient(null);
        termin.setStatus(TerminStatus.FREI);
        terminRepository.save(termin);
    }


}



