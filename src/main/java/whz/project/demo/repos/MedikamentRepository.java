package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whz.project.demo.entity.Medikament;

import java.util.Optional;

@Repository
public interface MedikamentRepository extends JpaRepository<Medikament,Long> {
    Optional<Medikament> findById(Long id);
    Optional<Medikament> findByName(String name);
}
