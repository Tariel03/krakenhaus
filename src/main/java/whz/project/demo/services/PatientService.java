package whz.project.demo.services;

import ch.qos.logback.classic.encoder.JsonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Patient;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.repos.PatientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundByIdException("Patient mit ID " + id + " nicht gefunden"));
    }

    public boolean existsById(Long id) {
        return patientRepository.existsById(id);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public void save(Patient patient) {
        patientRepository.save(patient);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return patientRepository.existsByEmailAndIdNot(email, id);
    }


}
