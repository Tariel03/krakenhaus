package whz.project.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Termin;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.repos.TerminRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerminService {
    private final TerminRepository terminRepository;
    private final BenutzerRepository benutzerRepository;
    private final BenutzerService benutzerService;

    public List<Termin> findAllByArzt(Benutzer arzt){
        return terminRepository.findByArzt(arzt);
    }
    public void generateDailyTermine(Benutzer arzt, LocalDate date) {
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 30);

        while (start.isBefore(end)) {
            Termin termin = Termin.builder()
                    .arzt(arzt)
                    .datum(date)
                    .uhrzeit(start)
                    .frei(true)
                    .build();

            terminRepository.save(termin);
            start = start.plusMinutes(30);
        }
    }


}
