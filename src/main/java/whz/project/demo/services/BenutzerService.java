package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.BenutzerDto;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Fachrictung;
import whz.project.demo.entity.Review;
import whz.project.demo.enums.Role;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BenutzerService {
    private final BenutzerRepository benutzerRepository;
    private final ReviewRepository reviewRepository;

    public List<Benutzer> findAllByRole(Role role){
        return benutzerRepository.findAllByRole(role);
    }
    public Benutzer findById(Long id) throws Exception{
        return benutzerRepository.findById(id).orElseThrow(() -> new Exception("No user by id : "+ id));
    }
    public List<BenutzerDto> getAllArztOverviews() {
        List<Benutzer> arzte = findAllByRole(Role.ARZT);

        return arzte.stream().map(arzt -> {
            List<Review> reviews = reviewRepository.findByArzt_Id(arzt.getId());

            double avg = reviews != null && !reviews.isEmpty()
                    ? reviews.stream()
                    .mapToInt(Review::getReview)
                    .average()
                    .orElse(0)
                    : 0;

            return BenutzerDto.builder()
                    .id(arzt.getId())
                    .vorname(arzt.getVorname())
                    .nachname(arzt.getNachname())
                    .fachrichtungen(arzt.getFachrictungList().stream()
                            .map(Fachrictung::getName)
                            .toList())
                    .averageReview(avg > 0 ? avg : null)
                    .build();
        }).toList();
    }

}
