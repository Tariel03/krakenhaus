package whz.project.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
