package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Medikament;
import whz.project.demo.entity.Rezept;
import whz.project.demo.repos.MedikamentRepository;
import whz.project.demo.repos.RezeptRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedikamentService {
    private final MedikamentRepository medikamentRepository;
    private final RezeptRepository rezeptRepository;


    public List<Medikament> findAll() {
        return medikamentRepository.findAll();
    }

    public Medikament findById(Long id) {
        return medikamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medikament not found with ID: " + id));
    }

    public MedikamentDto save(MedikamentDto dto) {
        Rezept rezept = rezeptRepository.findById(dto.getRezeptId())
                .orElseThrow(() -> new RuntimeException("Rezept nicht gefunden"));

        Medikament med = new Medikament();
        med.setName(dto.getName());
        med.setDosierung(dto.getDosierung());
        med.setBeschreibung(dto.getBeschreibung());
        med.setHersteller(dto.getHersteller());
        med.setRezept(rezept);

        medikamentRepository.save(med);

        dto.setId(med.getId());
        return dto;
    }





}
