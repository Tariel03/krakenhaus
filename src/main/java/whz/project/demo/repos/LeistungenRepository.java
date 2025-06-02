package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Leistungen;

import java.util.List;
import java.util.Optional;

public interface LeistungenRepository extends JpaRepository<Leistungen, Long> {

    List<Leistungen> findByArzt(Arzt arzt);
    Optional<Leistungen> findByIdAndArztId(Long id, Long arztId);


}