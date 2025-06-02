package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final BenutzerService benutzerService;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Benutzer benutzer = benutzerService.findById(userId);

        if (!passwordEncoder.matches(currentPassword, benutzer.getPassword())) {
            throw new IllegalArgumentException("Das aktuelle Passwort ist falsch.");
        }

        benutzer.setPassword(passwordEncoder.encode(newPassword));
        benutzerService.save(benutzer);
    }
}
