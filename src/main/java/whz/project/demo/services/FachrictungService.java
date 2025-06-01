package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.FachrictungDto;
import whz.project.demo.entity.Fachrictung;
import whz.project.demo.repos.FachrictungRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FachrictungService {
    private final FachrictungRepository fachrictungRepository;

    public List<Fachrictung> findAll() {
        return fachrictungRepository.findAll();
    }

    public Fachrictung findById(Long id) {
        return fachrictungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fachrichtung not found with ID: " + id));
    }

    public Fachrictung save(FachrictungDto dto) {
        Fachrictung fachrichtung = Fachrictung.builder()
                .name(dto.getName())
                .beschreibung(dto.getBeschreibung())
                .build();
        return fachrictungRepository.save(fachrichtung);
    }

    public Fachrictung update(Long id,FachrictungDto dto) {
        Fachrictung fachrichtung = findById(id);
        fachrichtung.setName(dto.getName());
        fachrichtung.setBeschreibung(dto.getBeschreibung());
        return fachrictungRepository.save(fachrichtung);
    }

    public void delete(Long id) {
        if (!fachrictungRepository.existsById(id)) {
            throw new IllegalArgumentException("Fachrichtung not found with ID: " + id);
        }
       fachrictungRepository.deleteById(id);
    }

    public List<Fachrictung> findAllByIds(List<Long > ids){
        return fachrictungRepository.findAllById(ids);

    }
}
