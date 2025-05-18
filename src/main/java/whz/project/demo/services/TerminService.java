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
    public void bookTermin(Long arztId, Long patientId, LocalDate datum, LocalTime uhrzeit) {
        if (datum.getDayOfWeek() == DayOfWeek.SATURDAY || datum.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Keine Termine am Wochenende.");
        }

        if (uhrzeit.isBefore(LocalTime.of(9, 0)) || uhrzeit.isAfter(LocalTime.of(17, 30))) {
            throw new IllegalArgumentException("Terminzeit muss zwischen 09:00 und 18:00 Uhr liegen.");
        }

        Benutzer benutzer = benutzerRepository.findById(arztId).orElse(null);
        boolean alreadyBooked = terminRepository.existsByArztAndDatumAndUhrzeit(benutzer, datum, uhrzeit);
        if (alreadyBooked) {
            throw new IllegalStateException("Arzt ist zu diesem Zeitpunkt bereits belegt.");
        }

        Termin termin = Termin.builder()
                .arzt(benutzerRepository.findById(arztId).orElseThrow())
                .patient(benutzerRepository.findById(patientId).orElseThrow())
                .datum(datum)
                .uhrzeit(uhrzeit)
                .frei(false)
                .build();

        terminRepository.save(termin);
    }

}
