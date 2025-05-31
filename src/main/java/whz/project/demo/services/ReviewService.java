package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import whz.project.demo.dto.ReviewDto;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Review;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BenutzerRepository benutzerRepository;

    private final CurrentUserService currentUserService;
    public void save(ReviewDto dto) {
        Benutzer arzt = benutzerRepository.findById(dto.getArzt_id())
                .orElseThrow(() -> new NotFoundByIdException("Arzt not found"));
        Benutzer patient = benutzerRepository.findById(dto.getPatient_id()).orElseThrow(() -> new NotFoundByIdException("Patient not found"));
        Review review = Review.builder()
                .comment(dto.getComment())
                .review(dto.getReview())
                .arzt(arzt)
                .patient(patient)
                .build();

        reviewRepository.save(review);
    }

    public int countByArzt(Benutzer arzt) {
        return reviewRepository.countReviewsByArzt(arzt);

    }


    public List<Review> getLastFiveReviews(Long arzt_id){
        return reviewRepository.findTop5ByArzt_IdOrderByDatumDesc(arzt_id);
    }


    public List<Review> findByArztId(Long arztId) {
        return reviewRepository.findByArzt_Id(arztId);
    }

    public List<Review> findByPatientId(Long patientId) {
        return reviewRepository.findByPatient_Id(patientId);
    }
}
