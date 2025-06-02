package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.FachrichtungDto;
import whz.project.demo.entity.Fachrichtung;
import whz.project.demo.repos.FachrichtungRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FachrichtungService {
    private final FachrichtungRepository fachrichtungRepository;

    public List<Fachrichtung> findAll() {
        return fachrichtungRepository.findAll();
    }

    public Fachrichtung findById(Long id) {
        return fachrichtungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fachrichtung not found with ID: " + id));
    }

    public Fachrichtung save(FachrichtungDto dto) {
        Fachrichtung fachrichtung = Fachrichtung.builder()
                .name(dto.getName())
                .beschreibung(dto.getBeschreibung())
                .build();
        return fachrichtungRepository.save(fachrichtung);
    }

    public Fachrichtung update(Long id,FachrichtungDto dto) {
        Fachrichtung fachrichtung = findById(id);
        fachrichtung.setName(dto.getName());
        fachrichtung.setBeschreibung(dto.getBeschreibung());
        return fachrichtungRepository.save(fachrichtung);
    }

    public void delete(Long id) {
        if (!fachrichtungRepository.existsById(id)) {
            throw new IllegalArgumentException("Fachrichtung not found with ID: " + id);
        }
       fachrichtungRepository.deleteById(id);
    }

    public List<Fachrichtung> findAllByIds(List<Long > ids){
        return fachrichtungRepository.findAllById(ids);

    }
}
