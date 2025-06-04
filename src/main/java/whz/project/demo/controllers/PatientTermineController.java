package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.CancelTerminRequestDto;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient/")
public class PatientTermineController {

    private final TerminService terminService;
    private final PatientService patientService;
    private final CurrentUserService currentUserService;

    /**
     * Отображение страницы с терминами пациента
     */
    @GetMapping("/termine")
    public String termine(Model model, Authentication authentication) {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return "redirect:/access-denied";
        }

        try {
            Patient patient = patientService.findById(userId);
            List<Termin> allTermine = terminService.findAllByPatient(patient);

            // Разделение на будущие и прошедшие термины
            LocalDate today = LocalDate.now();
            List<Termin> futureTermine = allTermine.stream()
                    .filter(termin -> termin.getDatum().isAfter(today) ||
                            (termin.getDatum().isEqual(today) &&
                                    termin.getUhrzeit().isAfter(java.time.LocalTime.now())))
                    .sorted((t1, t2) -> {
                        int dateCompare = t1.getDatum().compareTo(t2.getDatum());
                        return dateCompare != 0 ? dateCompare : t1.getUhrzeit().compareTo(t2.getUhrzeit());
                    })
                    .toList();

            List<Termin> pastTermine = allTermine.stream()
                    .filter(termin -> termin.getDatum().isBefore(today) ||
                            (termin.getDatum().isEqual(today) &&
                                    termin.getUhrzeit().isBefore(java.time.LocalTime.now())))
                    .sorted((t1, t2) -> {
                        int dateCompare = t2.getDatum().compareTo(t1.getDatum()); // Обратный порядок
                        return dateCompare != 0 ? dateCompare : t2.getUhrzeit().compareTo(t1.getUhrzeit());
                    })
                    .toList();

            model.addAttribute("patient", patient);
            model.addAttribute("futureTermine", futureTermine);
            model.addAttribute("pastTermine", pastTermine);

            return "patient/termine";

        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    /**
     * API endpoint для получения всех терминов пациента в JSON формате
     */
    @GetMapping("/termine/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTermineApi(Authentication authentication) {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Patient patient = patientService.findById(userId);
            List<Termin> allTermine = terminService.findAllByPatient(patient);

            LocalDate today = LocalDate.now();

            List<Map<String, Object>> futureTermine = allTermine.stream()
                    .filter(termin -> termin.getDatum().isAfter(today) ||
                            (termin.getDatum().isEqual(today) &&
                                    termin.getUhrzeit().isAfter(java.time.LocalTime.now())))
                    .sorted((t1, t2) -> {
                        int dateCompare = t1.getDatum().compareTo(t2.getDatum());
                        return dateCompare != 0 ? dateCompare : t1.getUhrzeit().compareTo(t2.getUhrzeit());
                    })
                    .map(this::terminToMap)
                    .toList();

            List<Map<String, Object>> pastTermine = allTermine.stream()
                    .filter(termin -> termin.getDatum().isBefore(today) ||
                            (termin.getDatum().isEqual(today) &&
                                    termin.getUhrzeit().isBefore(java.time.LocalTime.now())))
                    .sorted((t1, t2) -> {
                        int dateCompare = t2.getDatum().compareTo(t1.getDatum());
                        return dateCompare != 0 ? dateCompare : t2.getUhrzeit().compareTo(t1.getUhrzeit());
                    })
                    .map(this::terminToMap)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("futureTermine", futureTermine);
            response.put("pastTermine", pastTermine);
            response.put("totalCount", allTermine.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отмена будущего термина пациентом
     */
    @PostMapping("/termine/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cancelTermin(
            @RequestBody CancelTerminRequestDto request,
            Authentication authentication) {

        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("status", "error", "message", "Zugriff verweigert"));
        }

        try {
            Termin termin = terminService.findById(request.getTerminId());

            // Проверяем, принадлежит ли термин этому пациенту
            if (termin.getPatient() == null || !termin.getPatient().getId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("status", "error", "message", "Dieser Termin gehört nicht zu Ihnen"));
            }

            // Проверяем, что термин в будущем
            LocalDate today = LocalDate.now();
            if (termin.getDatum().isBefore(today) ||
                    (termin.getDatum().isEqual(today) &&
                            termin.getUhrzeit().isBefore(java.time.LocalTime.now()))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "Vergangene Termine können nicht abgesagt werden"));
            }

            // Проверяем статус термина
            if (termin.getStatus() != TerminStatus.GEBUCHT) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "error", "message", "Nur gebuchte Termine können abgesagt werden"));
            }

            // Отменяем термин
            terminService.cancelTerminByPatient(request.getTerminId(), userId, request.getReason());

            return ResponseEntity.ok(Map.of("status", "success", "message", "Termin wurde erfolgreich abgesagt"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Fehler beim Absagen des Termins: " + e.getMessage()));
        }
    }

    /**
     * Получение деталей конкретного термина
     */
    @GetMapping("/termine/{terminId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTerminDetails(
            @PathVariable Long terminId,
            Authentication authentication) {

        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Termin termin = terminService.findById(terminId);

            // Проверяем, принадлежит ли термин этому пациенту
            if (termin.getPatient() == null || !termin.getPatient().getId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(terminToMap(termin));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Вспомогательный метод для преобразования Termin в Map
     */
    private Map<String, Object> terminToMap(Termin termin) {
        Map<String, Object> terminMap = new HashMap<>();
        terminMap.put("id", termin.getId());
        terminMap.put("datum", termin.getDatum().toString());
        terminMap.put("uhrzeit", termin.getUhrzeit().format(DateTimeFormatter.ofPattern("HH:mm")));
        terminMap.put("diagnose", termin.getDiagnose());
        terminMap.put("notizen", termin.getNotizen());
        terminMap.put("status", termin.getStatus().name());
        terminMap.put("erstelltAm", termin.getErstelltAm().toString());

        // Информация об враче
        if (termin.getArzt() != null) {
            Map<String, Object> arztMap = new HashMap<>();
            arztMap.put("id", termin.getArzt().getId());
            arztMap.put("name", termin.getArzt().getVorname() + " " +
                    termin.getArzt().getNachname());
            arztMap.put("fachrichtung", termin.getArzt().getFachrichtungList() != null &&
                    !termin.getArzt().getFachrichtungList().isEmpty() ?
                    termin.getArzt().getFachrichtungList().get(0).getName() :
                    "Allgemeinmedizin");
            terminMap.put("arzt", arztMap);
        }

        // Информация о пациенте
        if (termin.getPatient() != null) {
            Map<String, Object> patientMap = new HashMap<>();
            patientMap.put("name", termin.getPatient().getVorname() + " " +
                    termin.getPatient().getNachname());
            terminMap.put("patient", patientMap);
        }

        return terminMap;
    }
}