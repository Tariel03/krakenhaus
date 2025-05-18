package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Fachrictung;

import java.util.List;

@Repository
public interface FachrictungRepository extends JpaRepository<Fachrictung, Long> {
    List<Fachrictung> findFachrictungByBenutzerList(List<Benutzer> benutzerList);
}
