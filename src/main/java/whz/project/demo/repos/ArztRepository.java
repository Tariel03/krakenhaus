package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import whz.project.demo.entity.Arzt;

public interface ArztRepository extends JpaRepository<Arzt, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
}