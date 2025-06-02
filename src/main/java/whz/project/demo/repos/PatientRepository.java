package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
}