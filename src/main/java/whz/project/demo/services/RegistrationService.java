package whz.project.demo.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;
import whz.project.demo.excpetions.NotFoundByLoginException;
import whz.project.demo.repos.BenutzerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final BenutzerRepository benutzerRepository;

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
        if(benutzerRepository.findByLogin(benutzer.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Benutzer mit diesem Login existiert bereits");
        }
        benutzer.setNachname(benutzer.getNachname());
        benutzer.setVorname(benutzer.getVorname());
        benutzer.setPassword(passwordEncoder.encode(benutzer.getPassword()));
        benutzer.setEmail(benutzer.getEmail());
        benutzer.setRole(Role.PATIENT); // or whatever default role you want
        benutzerRepository.save(benutzer);
    }


}
