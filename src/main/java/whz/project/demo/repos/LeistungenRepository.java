package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Leistungen;

public interface LeistungenRepository extends JpaRepository<Leistungen, Long> {
}