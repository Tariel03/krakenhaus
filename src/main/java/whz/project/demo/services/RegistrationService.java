package whz.project.demo.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByLoginException;
import whz.project.demo.repos.BenutzerRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final BenutzerRepository benutzerRepository;
    private final TerminService terminService;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public Benutzer findByLogin(String login) throws NotFoundByLoginException{
        Optional<Benutzer> benutzer = benutzerRepository.findByLogin(login);
        if (benutzer.isEmpty()) {
            throw new NotFoundByLoginException("No user found by this login: "+ login);
        }
        return benutzer.get();

    }
    public void save(Benutzer benutzer) throws NotFoundByLoginException {
        Optional<Benutzer> optionalBenutzer = benutzerRepository.findByLogin(benutzer.getLogin());
        if(optionalBenutzer.isPresent()) {
            throw new IllegalArgumentException("Benutzer mit diesem Login existiert bereits");
        }
        benutzer.setPassword(passwordEncoder.encode(benutzer.getPassword()));

        benutzerRepository.save(benutzer);

    }


}
