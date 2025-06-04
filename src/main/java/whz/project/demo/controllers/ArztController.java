package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.ReviewDto;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.entity.Termin;
import whz.project.demo.services.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/arzt/")
public class ArztController {
    private final TerminService terminService;
    private final ArztService arztService;
    private final ReviewService reviewService;
    private final LeistungenService leistungenService;
    private final FachrichtungService fachrichtungService;
    private final CurrentUserService currentUserService;
    private final PatientService patientService;

    @GetMapping("/{id}")
    public String arzt(@PathVariable("id") Long id, Model model) throws Exception {
        Arzt arzt = arztService.findById(id);

        List<Termin> allTermine = terminService.findAllByArzt(arzt);
        List<Termin> bookedTermine = allTermine.stream()
                .filter(termin -> termin.getPatient() != null)
                .toList();

        model.addAttribute("arzt", arzt);
        model.addAttribute("leistungen", arzt.getLeistungen());
        model.addAttribute("fachrichtungen", fachrichtungService.findAllByIds(List.of(id)));
        model.addAttribute("reviews", reviewService.getLastFiveReviews(arzt.getId()));
        model.addAttribute("bookedTermine", bookedTermine);

        return "arzt/arzt";
    }

    /**
     * Получение доступных терминов для конкретной даты
     */
    @GetMapping("/{id}/available-slots")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAvailableSlots(
            @PathVariable("id") Long arztId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            Arzt arzt = arztService.findById(arztId);
            List<Termin> availableTermine = terminService.findAllTermineByArztAndDate(arzt, date);

            List<Map<String, Object>> slots = availableTermine.stream()
                    .map(termin -> {
                        Map<String, Object> slot = new HashMap<>();
                        slot.put("id", termin.getId());
                        slot.put("time", termin.getUhrzeit().format(DateTimeFormatter.ofPattern("HH:mm")));
                        slot.put("status", termin.getStatus().name());
                        return slot;
                    })
                    .toList();

            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Бронирование термина
     */
    @PostMapping("/book-termin")
    @ResponseBody
    public String bookTermin(
            @RequestParam Long terminId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
        if (!patientService.existsById(userId)) {
            return "error: access-denied";
        }

        try {
            // Бронируем существующий термин по ID
            terminService.bookTermin(terminId, userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * Отмена бронирования термина
     */
    @PostMapping("/cancel-termin")
    @ResponseBody
    public String cancelTermin(@RequestParam Long terminId, Authentication authentication) {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
        if (!patientService.existsById(userId)) {
            return "error: access-denied";
        }

        try {
            terminService.cancelTermin(terminId, userId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/review")
    public String addReview(@ModelAttribute ReviewDto reviewDto, Authentication authentication) {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return "redirect:/access-denied";
        }

        reviewDto.setPatient_id(userId);

        try {
            reviewService.save(reviewDto);
        } catch (NotFoundByIdException e) {
            return "redirect:/error";
        }

        return "redirect:/arzt/" + reviewDto.getArzt_id();
    }

}