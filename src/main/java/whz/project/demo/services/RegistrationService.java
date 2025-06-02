package whz.project.demo.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByLoginException;
import whz.project.demo.repos.ArztRepository;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.PatientRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArztRepository arztRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Benutzer findByLogin(String login) throws NotFoundByLoginException{
        Optional<Benutzer> benutzer = benutzerRepository.findByLogin(login);
        if (benutzer.isEmpty()) {
            throw new NotFoundByLoginException("No user found by this login: "+ login);
        }
        return benutzer.get();

    }
    public void registriereArzt(Arzt arzt) {
        if (benutzerRepository.existsByLogin(arzt.getLogin())) {
            throw new IllegalArgumentException("Login ist bereits vergeben.");
        }


        arzt.setActive(false);
        arzt.setRole(Role.ARZT);
        arzt.setPassword(passwordEncoder.encode(arzt.getPassword()));

        arztRepository.save(arzt);
    }


    public void registrierePatient(Patient patient) {
        if (benutzerRepository.existsByLogin(patient.getLogin())) {
            throw new IllegalArgumentException("Login ist bereits vergeben.");
        }


        patient.setActive(true);
        patient.setRole(Role.PATIENT);
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));

        patientRepository.save(patient);
    }


}
