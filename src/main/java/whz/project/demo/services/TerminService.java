package whz.project.demo.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.repos.TerminRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TerminService {
    private final TerminRepository terminRepository;
    private final PatientService patientService;
    private final ArztService arztService;

    public List<Termin> findAllByArzt(Arzt arzt){
        return terminRepository.findByArzt(arzt);
    }
    public List<Termin> findAllByPatient(Patient patient) {
        return terminRepository.findByPatientOrderByDatumDescUhrzeitDesc(patient);
    }


    public Termin findById(Long id) throws Exception {
        return terminRepository.findById(id)
                .orElseThrow(() -> new Exception("Терmin с ID " + id + " не найден"));
    }


    public void bookTermin(Long terminId, Long patientId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new IllegalArgumentException("Termin nicht gefunden"));

        if (termin.getStatus() != TerminStatus.FREI) {
            throw new IllegalStateException("Termin ist bereits belegt oder nicht verfügbar.");
        }

        Patient patient = patientService.findById(patientId);

        termin.setStatus(TerminStatus.GEBUCHT);
        termin.setPatient(patient);
        terminRepository.save(termin);
    }

    public List<Termin> findeAlleFuerArzt(Arzt arzt) {
        return terminRepository.findByArztId(arzt.getId());
    }


    public void statusAktualisieren(Long terminId, TerminStatus status, Long arztId) throws AccessDeniedException {
        Termin termin = findeMitZugriff(terminId, arztId);
        termin.setStatus(status);
        terminRepository.save(termin);
    }

    public Termin findeMitZugriff(Long terminId, Long arztId) throws AccessDeniedException {
        if (arztId == null) {
            throw new AccessDeniedException("Nicht autorisiert");
        }

        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));

        if (termin.getArzt() == null || !termin.getArzt().getId().equals(arztId)) {
            throw new AccessDeniedException("Kein Zugriff auf diesen Termin");
        }

        return termin;
    }


    public Termin speichern(Termin termin) {
        return terminRepository.save(termin);
    }


    public void storniereTermin(Long terminId, Long patientId) throws AccessDeniedException {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));

        if (termin.getPatient() == null || !termin.getPatient().getId().equals(patientId)) {
            throw new AccessDeniedException("Kein Zugriff zum Stornieren dieses Termins");
        }

        termin.setPatient(null);
        termin.setStatus(TerminStatus.FREI);
        terminRepository.save(termin);
    }

    public List<Termin> findAvailableTermineByArztAndDate(Arzt arzt, LocalDate date) {
        return terminRepository.findByArztAndDatumAndStatus(arzt, date, TerminStatus.FREI);
    }

    public List<Termin> findAllTermineByArztAndDate(Arzt arzt, LocalDate date) {
        return terminRepository.findByArztAndDatum(arzt, date);
    }

    public Termin createTermin(Long arztId, LocalDate date, LocalTime time, Long patientId) {
        try {
            Arzt arzt = arztService.findById(arztId);
            Patient patient = patientService.findById(patientId);

            // Проверяем, что термин на это время не существует
            Optional<Termin> existingTermin = terminRepository
                    .findByArztAndDatumAndUhrzeit(arzt, date, time);

            if (existingTermin.isPresent()) {
                throw new RuntimeException("Termin bereits vorhanden");
            }

            Termin newTermin = Termin.builder()
                    .arzt(arzt)
                    .patient(patient)
                    .datum(date)
                    .uhrzeit(time)
                    .status(TerminStatus.GEBUCHT)
                    .build();

            return terminRepository.save(newTermin);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Erstellen des Termins: " + e.getMessage());
        }
    }

    public void cancelTermin(Long terminId, Long patientId) {
        try {
            Termin termin = terminRepository.findById(terminId)
                    .orElseThrow(() -> new RuntimeException("Termin nicht gefunden"));

            // Проверяем, что термин принадлежит этому пациенту
            if (termin.getPatient() == null || !termin.getPatient().getId().equals(patientId)) {
                throw new RuntimeException("Sie können nur Ihre eigenen Termine stornieren");
            }

            if (termin.getStatus() == TerminStatus.ABGESCHLOSSEN) {
                throw new RuntimeException("Abgeschlossene Termine können nicht storniert werden");
            }

            // Проверяем, что термин не слишком близко (например, минимум 24 часа до термина)
            LocalDateTime terminDateTime = LocalDateTime.of(termin.getDatum(), termin.getUhrzeit());
            if (terminDateTime.isBefore(LocalDateTime.now().plusHours(24))) {
                throw new RuntimeException("Termine können nur 24 Stunden im Voraus storniert werden");
            }

            termin.setPatient(null);
            termin.setStatus(TerminStatus.FREI);
            termin.setDiagnose(null);
            termin.setNotizen(null);

            terminRepository.save(termin);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Stornieren des Termins: " + e.getMessage());
        }

    }

    public List<Termin> findTermineByPatient(Long patientId) {
        Patient patient = patientService.findById(patientId);
        return terminRepository.findByPatientOrderByDatumAscUhrzeitAsc(patient);
    }

    /**
     * Получение будущих терминов пациента
     */
    public List<Termin> findUpcomingTermineByPatient(Long patientId) {
        Patient patient = patientService.findById(patientId);
        LocalDate today = LocalDate.now();
        return terminRepository.findByPatientAndDatumGreaterThanEqualOrderByDatumAscUhrzeitAsc(patient, today);
    }

    /**
     * Получение терминов врача на неделю
     */
    public List<Termin> findTermineByArztAndWeek(Arzt arzt, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return terminRepository.findByArztAndDatumBetweenOrderByDatumAscUhrzeitAsc(arzt, weekStart, weekEnd);
    }

    /**
     * Создание стандартных временных слотов для врача на определенную дату
     */
    public void createStandardSlotsForDate(Long arztId, LocalDate date) {
        try {
            Arzt arzt = arztService.findById(arztId);

            // Проверяем, что слоты еще не созданы для этой даты
            List<Termin> existingTermine = findAllTermineByArztAndDate(arzt, date);
            if (!existingTermine.isEmpty()) {
                throw new RuntimeException("Termine für dieses Datum bereits vorhanden");
            }

            // Стандартные временные слоты (можно сделать конфигурируемыми)
            List<LocalTime> standardTimes = List.of(
                    LocalTime.of(8, 0),
                    LocalTime.of(8, 30),
                    LocalTime.of(9, 0),
                    LocalTime.of(9, 30),
                    LocalTime.of(10, 0),
                    LocalTime.of(10, 30),
                    LocalTime.of(11, 0),
                    LocalTime.of(11, 30),
                    LocalTime.of(14, 0),
                    LocalTime.of(14, 30),
                    LocalTime.of(15, 0),
                    LocalTime.of(15, 30),
                    LocalTime.of(16, 0),
                    LocalTime.of(16, 30)
            );

            // Пропускаем воскресенье
            if (date.getDayOfWeek().getValue() == 7) {
                return;
            }

            List<Termin> newTermine = standardTimes.stream()
                    .map(time -> Termin.builder()
                            .arzt(arzt)
                            .datum(date)
                            .uhrzeit(time)
                            .status(TerminStatus.FREI)
                            .build())
                    .toList();

            terminRepository.saveAll(newTermine);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Erstellen der Standard-Slots: " + e.getMessage());
        }
    }


    @Transactional
    public void cancelTerminByPatient(Long terminId, Long patientId, String reason) throws Exception {
        Termin termin = findById(terminId);

        // Проверяем, что термин принадлежит пациенту
        if (termin.getPatient() == null || !termin.getPatient().getId().equals(patientId)) {
            throw new SecurityException("Терmin не принадлежит данному пациенту");
        }

        // Проверяем, что термин можно отменить
        if (termin.getStatus() != TerminStatus.GEBUCHT) {
            throw new IllegalStateException("Можно отменить только забронированные термины");
        }

        // Проверяем, что термин в будущем
        LocalDate today = LocalDate.now();
        if (termin.getDatum().isBefore(today) ||
                (termin.getDatum().isEqual(today) &&
                        termin.getUhrzeit().isBefore(LocalTime.now()))) {
            throw new IllegalStateException("Нельзя отменить прошедший термин");
        }

        // Отменяем термин
        termin.setStatus(TerminStatus.ABGESAGT);
        termin.setPatient(null); // Освобождаем термин

        // Добавляем причину отмены в заметки
        if (reason != null && !reason.trim().isEmpty()) {
            String currentNotes = termin.getNotizen() != null ? termin.getNotizen() : "";
            termin.setNotizen(currentNotes + (currentNotes.isEmpty() ? "" : " | ") +
                    "Abgesagt vom Patienten: " + reason.trim());
        } else {
            String currentNotes = termin.getNotizen() != null ? termin.getNotizen() : "";
            termin.setNotizen(currentNotes + (currentNotes.isEmpty() ? "" : " | ") +
                    "Abgesagt vom Patienten");
        }

        terminRepository.save(termin);
    }


}



