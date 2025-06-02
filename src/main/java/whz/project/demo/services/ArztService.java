package whz.project.demo.services;

import ch.qos.logback.classic.encoder.JsonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import whz.project.demo.Photos.PhotoConfig;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Review;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.repos.ArztRepository;
import whz.project.demo.repos.ReviewRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArztService {

    private final ArztRepository arztRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhotoConfig photoConfig;

    public Arzt findById(Long id) {
        return arztRepository.findById(id)
                .orElseThrow(() -> new NotFoundByIdException("Arzt mit ID " + id + " nicht gefunden"));
    }

    public List<Arzt> findAll() {
        return arztRepository.findAll();
    }

    public void save(Arzt arzt) {
        arztRepository.save(arzt);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return arztRepository.existsByEmailAndIdNot(email, id);
    }

    public double getAverageReview(Long arztId) {
        List<Review> reviews = reviewRepository.findByArzt_Id(arztId);

        if (!reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToDouble(Review::getReview)
                    .average()
                    .orElse(0.0);
            return Math.round(avg * 10.0) / 10.0;
        }

        return 0.0;
    }
    public void aktualisiereProfil(Long id, Arzt neuerWert, MultipartFile bild) throws IOException {
        Arzt arzt = arztRepository.findById(id).orElseThrow();

        arzt.setTitel(neuerWert.getTitel());
        arzt.setVorname(neuerWert.getVorname());
        arzt.setNachname(neuerWert.getNachname());
        arzt.setEmail(neuerWert.getEmail());
        arzt.setTelefonnummer(neuerWert.getTelefonnummer());
        arzt.setAddress(neuerWert.getAddress());
        arzt.setGeburtsdatum(neuerWert.getGeburtsdatum());
        arzt.setBeschreibung(neuerWert.getBeschreibung());
        arzt.setFachrichtungList(neuerWert.getFachrichtungList());
        arzt.setGeschlecht(neuerWert.getGeschlecht());
        if (bild != null && !bild.isEmpty()) {
            String filename = photoConfig.savePhoto(bild);
            arzt.setMainImage("/img/" + filename);
        }

        arztRepository.save(arzt);
    }






}
