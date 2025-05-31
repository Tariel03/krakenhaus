package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Termin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TerminRepository extends JpaRepository<Termin, Long> {
    List<Termin> findByArzt(Benutzer arzt);
    List<Termin> findByPatient(Benutzer benutzer);
    Boolean existsByArztAndDatumAndUhrzeit(Benutzer arzt, LocalDate date, LocalTime time);
    List<Termin> findByArztId(Long arztId);


}
