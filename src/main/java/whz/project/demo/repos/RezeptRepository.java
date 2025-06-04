package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Rezept;
import whz.project.demo.entity.Termin;

import java.util.List;
import java.util.Optional;

public interface RezeptRepository extends JpaRepository<Rezept, Long> {
    List<Rezept> findByTermin_Id(Long terminId);
    Optional<Rezept> findByTermin(Termin termin);
}