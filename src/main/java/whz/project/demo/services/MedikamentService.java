package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Medikament;
import whz.project.demo.repos.MedikamentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedikamentService {
    private final MedikamentRepository medikamentRepository;

    public void save(MedikamentDto medikamentDto) {
        medikamentRepository.save(Medikament.builder()
                .name(medikamentDto.getName())
                .beschreibung(medikamentDto.getBeschreibung())
                .hersteller(medikamentDto.getHersteller())
                .build());
    }


    public List<Medikament> findAll() {
        return medikamentRepository.findAll();
    }

    public Medikament findById(Long id) {
        return medikamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medikament not found with ID: " + id));
    }


    public Medikament update(Long id, MedikamentDto dto) {
        Medikament med = findById(id);
        med.setName(dto.getName());
        med.setBeschreibung(dto.getBeschreibung());
        med.setHersteller(dto.getHersteller());
        return medikamentRepository.save(med);
    }



}
