package whz.project.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.repos.LeistungenRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LeistungenService {

    private final LeistungenRepository leistungenRepository;

    @Autowired
    public LeistungenService(LeistungenRepository leistungenRepository) {
        this.leistungenRepository = leistungenRepository;
    }

    public List<Leistungen> findAll() {
        return leistungenRepository.findAll();
    }

    public Optional<Leistungen> findById(Long id) {
        return leistungenRepository.findById(id);
    }

    public List<Leistungen> findAllByIds(List<Long> ids) {
        return leistungenRepository.findAllById(ids);
    }

    public Leistungen save(Leistungen leistung) {
        return leistungenRepository.save(leistung);
    }

    public List<Leistungen> findByArzt(Arzt arzt) {
        return leistungenRepository.findByArzt(arzt);
    }


    public Leistungen findeByIdUndArzt(Long id, Long arztId) {
        return leistungenRepository.findByIdAndArztId(id, arztId)
                .orElseThrow(() -> new RuntimeException("Nicht gefunden"));
    }

    public void loescheByIdUndArzt(Long id, Long arztId) {
        Leistungen leistung = findeByIdUndArzt(id, arztId);
        leistungenRepository.delete(leistung);
    }

}
