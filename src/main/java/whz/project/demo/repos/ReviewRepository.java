package whz.project.demo.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, PagingAndSortingRepository<Review, Long> {
    List<Review> findByArzt_Id(Long arztId);
    List<Review> findTop5ByArzt_IdOrderByDatumDesc(Long arztId);
    List<Review> findByPatient_Id(Long patientId);
    boolean existsByArztAndPatient(Arzt arzt, Patient patient);
    int countReviewsByArzt(Arzt arzt);
    Page<Review> findReviewsByArztId(Long arzt_id, Pageable pageable);

}
