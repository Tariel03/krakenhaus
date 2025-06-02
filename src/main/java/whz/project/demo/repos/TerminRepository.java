package whz.project.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TerminRepository extends JpaRepository<Termin, Long> {
    List<Termin> findByArzt(Arzt arzt);
    List<Termin> findByPatient(Patient patient);
    Boolean existsByArztAndDatumAndUhrzeit(Arzt arzt, LocalDate date, LocalTime time);
    List<Termin> findByArztId(Long arztId);
    List<Termin> findAllByArzt(Arzt arzt);
    List<Termin> findByArztAndDatumAndStatus(Arzt arzt, LocalDate datum, TerminStatus status);
    List<Termin> findByArztAndDatum(Arzt arzt, LocalDate datum);
    Optional<Termin> findByArztAndDatumAndUhrzeit(Arzt arzt, LocalDate datum, LocalTime uhrzeit);
    List<Termin> findByPatientAndDatumGreaterThanEqualOrderByDatumAscUhrzeitAsc(Patient patient, LocalDate datum);
    List<Termin> findByArztAndDatumBetweenOrderByDatumAscUhrzeitAsc(Arzt arzt, LocalDate startDate, LocalDate endDate);
    List<Termin> findByArztAndDatumOrderByUhrzeitAsc(Arzt arzt, LocalDate datum);
    List<Termin> findByArztAndStatusAndDatumGreaterThanEqualOrderByDatumAscUhrzeitAsc(
            Arzt arzt, TerminStatus status, LocalDate datum);
    @Query("SELECT COUNT(t) FROM Termin t WHERE t.arzt = :arzt AND t.datum = :datum AND t.uhrzeit = :uhrzeit AND t.status != 'FREI'")
    Long countConflictingTermine(@Param("arzt") Arzt arzt,
                                 @Param("datum") LocalDate datum,
                                 @Param("uhrzeit") LocalTime uhrzeit);
    List<Termin> findByPatientAndStatusOrderByDatumAscUhrzeitAsc(Patient patient, TerminStatus status);
    List<Termin> findByArztAndStatusAndDatumBetweenOrderByDatumAscUhrzeitAsc(
            Arzt arzt, TerminStatus status, LocalDate startDate, LocalDate endDate);
    @Query("SELECT COUNT(t) FROM Termin t WHERE t.arzt = :arzt AND t.status = 'GEBUCHT' AND t.datum BETWEEN :startDate AND :endDate")
    Long countBookedTermineInPeriod(@Param("arzt") Arzt arzt,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    List<Termin> findTop10ByPatientAndStatusOrderByDatumDescUhrzeitDesc(Patient patient, TerminStatus status);

    /**
     * Поиск всех терминов пациента, отсортированных по дате и времени (сначала новые)
     */
    @Query("SELECT t FROM Termin t WHERE t.patient = :patient ORDER BY t.datum DESC, t.uhrzeit DESC")
    List<Termin> findByPatientOrderByDatumDescUhrzeitDesc(@Param("patient") Patient patient);

    /**
     * Поиск всех терминов пациента, отсортированных по дате и времени (сначала старые)
     */
    @Query("SELECT t FROM Termin t WHERE t.patient = :patient ORDER BY t.datum ASC, t.uhrzeit ASC")
    List<Termin> findByPatientOrderByDatumAscUhrzeitAsc(@Param("patient") Patient patient);

    /**
     * Поиск будущих терминов пациента
     */
    @Query("SELECT t FROM Termin t WHERE t.patient = :patient AND " +
            "(t.datum > :today OR (t.datum = :today AND t.uhrzeit > :currentTime)) " +
            "ORDER BY t.datum ASC, t.uhrzeit ASC")
    List<Termin> findFutureTermineByPatient(@Param("patient") Patient patient,
                                            @Param("today") LocalDate today,
                                            @Param("currentTime") LocalTime currentTime);

    /**
     * Поиск прошедших терминов пациента
     */
    @Query("SELECT t FROM Termin t WHERE t.patient = :patient AND " +
            "(t.datum < :today OR (t.datum = :today AND t.uhrzeit < :currentTime)) " +
            "ORDER BY t.datum DESC, t.uhrzeit DESC")
    List<Termin> findPastTermineByPatient(@Param("patient") Patient patient,
                                          @Param("today") LocalDate today,
                                          @Param("currentTime") LocalTime currentTime);
}
