package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Review;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.ReviewRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BenutzerService {
    private final BenutzerRepository benutzerRepository;

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return benutzerRepository.existsByEmailAndIdNot(email, id);
    }

    public Benutzer findById(Long id) {
        return benutzerRepository.findById(id)
                .orElseThrow(() -> new NotFoundByIdException("Benutzer mit ID " + id + " nicht gefunden"));
    }


    public Benutzer findByLogin(String login) {
        return benutzerRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden: " + login));
    }

    public void save(Benutzer benutzer) {
        benutzerRepository.save(benutzer);
    }
}
