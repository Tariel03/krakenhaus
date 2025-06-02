package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TerminRepository extends JpaRepository<Termin, Long> {
    List<Termin> findByArzt(Arzt arzt);
    List<Termin> findByPatient(Patient patient);
    Boolean existsByArztAndDatumAndUhrzeit(Arzt arzt, LocalDate date, LocalTime time);
    List<Termin> findByArztId(Long arztId);


}
