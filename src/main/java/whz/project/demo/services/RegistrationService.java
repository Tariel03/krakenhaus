package whz.project.demo.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;
import whz.project.demo.repos.BenutzerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final BenutzerRepository benutzerRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public Benutzer findByLogin(String login) throws Exception{
        Optional<Benutzer> benutzer = benutzerRepository.findByLogin(login);
        if (benutzer.isEmpty()) {
            throw new Exception("No user bu this login: "+ login);
        }
        return benutzer.get();

    }
    public void save(Benutzer benutzer) {
        benutzer.setPassword(passwordEncoder.encode(benutzer.getPassword()));
        benutzer.setRole(Role.PATIENT); // or whatever default role you want
        benutzerRepository.save(benutzer);
    }


}
