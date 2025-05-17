package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import whz.project.demo.entity.Benutzer;

import java.util.Optional;

public interface BenutzerRepository extends JpaRepository<Benutzer, Integer> {
    Optional<Benutzer> findByLogin(String username);
}
