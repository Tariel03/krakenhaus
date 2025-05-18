package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Rezept;

public interface RezeptRepository extends JpaRepository<Rezept, Long> {
}
