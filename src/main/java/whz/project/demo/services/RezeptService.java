package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
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
        if (rezept.getTermin() == null || rezept.getTermin().getId() == null) {
            throw new IllegalArgumentException("Termin oder Termin-ID fehlt im Rezept!");
        }

        Termin termin = terminService.findById(rezept.getTermin().getId());
        termin.setNotizen(notizen);
        termin.setDiagnose(diagnose);
        terminService.speichern(termin);

        List<Medikament> existingMedikamentList = new ArrayList<>();

        if (rezept.getId() != null) {
            Rezept existingRezept = rezeptRepository.findById(rezept.getId()).orElse(null);
            if (existingRezept != null && existingRezept.getMedikamentList() != null) {
                existingMedikamentList.addAll(existingRezept.getMedikamentList());
            }
        }

        rezept.setTermin(termin);
        rezept.setDatum_uhrzeit(LocalDateTime.now());

        if (rezept.getMedikamentList() != null) {
            for (Medikament med : rezept.getMedikamentList()) {
                med.setRezept(rezept);
                existingMedikamentList.add(med);
            }
        }

        rezept.setMedikamentList(existingMedikamentList);

        rezeptRepository.save(rezept);
    }


}
