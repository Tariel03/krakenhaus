package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Fachrichtung;

import java.util.List;

@Repository
public interface FachrichtungRepository extends JpaRepository<Fachrichtung, Long> {
    List<Fachrichtung> findByArztListContaining(Arzt arzt);

}
