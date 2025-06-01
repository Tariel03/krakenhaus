package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Review;
import whz.project.demo.enums.Role;
import whz.project.demo.exceptions.NotFoundByIdException;
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
    public double getAverageReviewForArzt(Long benutzer_id) {
        Benutzer benutzer = benutzerRepository.findById(benutzer_id)
                .orElseThrow(() -> new NotFoundByIdException("Not such user with id: " + benutzer_id));

        List<Review> reviews = reviewRepository.findByArzt_Id(benutzer_id);

        if (!reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToDouble(Review::getReview)
                    .average()
                    .orElse(0.0);

            return Math.round(avg * 10.0) / 10.0;
        }

        return 0.0;
    }

    public void save(Benutzer benutzer) {
        benutzerRepository.save(benutzer);
    }


    public boolean existsByEmailAndIdNot(String email, Long id) {
        return benutzerRepository.existsByEmailAndIdNot(email, id);
    }
}
