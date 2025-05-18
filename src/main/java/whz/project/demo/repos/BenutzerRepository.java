package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;

import java.util.List;
import java.util.Optional;

public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {
    Optional<Benutzer> findByLogin(String username);
    List<Benutzer> findAllByRole(Role role);
    Optional<Benutzer> findById(Long id);
}
