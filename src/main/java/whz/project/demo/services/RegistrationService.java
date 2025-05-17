package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.repos.BenutzerRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final BenutzerRepository benutzerRepository;


}
