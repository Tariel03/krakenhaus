package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Medikament;
import whz.project.demo.entity.Rezept;
import whz.project.demo.entity.Termin;
import whz.project.demo.repos.MedikamentRepository;
import whz.project.demo.repos.RezeptRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RezeptService {
    private final RezeptRepository rezeptRepository;
    private final TerminService terminService;
    private final MedikamentService medikamentService;
    private final MedikamentRepository medikamentRepository;


    public List<Rezept> findAll() {
        return rezeptRepository.findAll();
    }

    public Optional<Rezept> findById(Long id) {
        return rezeptRepository.findById(id);
    }

    public Rezept save(Rezept rezept) {
        return rezeptRepository.save(rezept);
    }

    public void deleteById(Long id) {
        rezeptRepository.deleteById(id);
    }

    public Rezept findOrCreateByTermin(Termin termin) {
        return rezeptRepository.findByTermin(termin)
                .orElseGet(() -> {
                    Rezept neu = new Rezept();
                    neu.setTermin(termin);
                    return neu;
                });
    }



    public List<Rezept> findByTerminId(Long terminId) {
        return rezeptRepository.findByTermin_Id(terminId);
    }



    public void speichereRezeptMitTerminInfos(Rezept rezept, String notizen, String diagnose) throws Exception {
        Termin termin = terminService.findById(rezept.getTermin().getId());
        termin.setNotizen(notizen);
        termin.setDiagnose(diagnose);
        terminService.speichern(termin);

        rezept.setTermin(termin);
        rezept.setDatum_uhrzeit(LocalDateTime.now());

        if (rezept.getMedikamentList() != null) {
            for (Medikament med : rezept.getMedikamentList()) {
                med.setRezept(rezept);
            }
        }

        rezeptRepository.save(rezept);
    }

    public MedikamentDto addMedikamentAjax(MedikamentDto dto) {
        Rezept rezept = rezeptRepository.findById(dto.getRezeptId())
                .orElseThrow(() -> new IllegalArgumentException("Rezept nicht gefunden"));

        Medikament med = new Medikament();
        med.setName(dto.getName());
        med.setDosierung(dto.getDosierung());
        med.setBeschreibung(dto.getBeschreibung());
        med.setHersteller(dto.getHersteller());
        med.setRezept(rezept);

        medikamentRepository.save(med);
        dto.setId(med.getId()); // wichtig für die Rückgabe
        return dto;
    }

    public void updateMedikament(MedikamentDto dto) {
        Medikament med = medikamentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Medikament nicht gefunden"));

        med.setName(dto.getName());
        med.setDosierung(dto.getDosierung());
        med.setBeschreibung(dto.getBeschreibung());
        med.setHersteller(dto.getHersteller());
        medikamentRepository.save(med);
    }

    public Long deleteMedikament(Long id) {
        Medikament med = medikamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medikament nicht gefunden"));
        Long rezeptId = med.getRezept().getId();
        medikamentRepository.delete(med);
        return rezeptId;
    }

}
