package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.ReviewDto;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Review;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BenutzerRepository benutzerRepository;
    private final CurrentUserService currentUserService;
    public Review save(ReviewDto dto) {
        Benutzer arzt = benutzerRepository.findById(dto.getArzt_id())
                .orElseThrow(() -> new RuntimeException("Arzt not found"));

        Review review = Review.builder()
                .comment(dto.getComment())
                .review(dto.getReview())
                .arzt(arzt)
                .patient(currentUserService.getCustomerIdFromPrincipal(P))
                .build();

        return reviewRepository.save(review);
    }


    public List<Review> findByArztId(Long arztId) {
        return reviewRepository.findByArzt_Id(arztId);
    }

    public List<Review> findByPatientId(Long patientId) {
        return reviewRepository.findByPatient_Id(patientId);
    }
}
