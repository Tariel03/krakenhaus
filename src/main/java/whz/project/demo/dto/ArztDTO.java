package whz.project.demo.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.entity.Fachrichtung;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArztDTO {

    private Long id;
    private String vorname;
    private String nachname;
    private String beschreibung;
    private String telefonnummer;
    private String address;
    private String mainImage;

    private List<String> leistungen;     // отдельные названия
    private String leistungenCSV;        // для JS фильтра
    private List<String> fachrichtungen; // отдельные названия

    public ArztDTO(Arzt arzt) {
        this.id = arzt.getId();
        this.vorname = arzt.getVorname();
        this.nachname = arzt.getNachname();
        this.beschreibung = arzt.getBeschreibung();
        this.telefonnummer = arzt.getTelefonnummer();
        this.address = arzt.getAddress();
        this.mainImage = arzt.getMainImage();

        this.leistungen = arzt.getLeistungen() != null ?
                arzt.getLeistungen().stream()
                        .map(Leistungen::getName)
                        .collect(Collectors.toList())
                : List.of();

        this.leistungenCSV = String.join(",", this.leistungen);

        this.fachrichtungen = arzt.getFachrichtungList() != null ?
                arzt.getFachrichtungList().stream()
                        .map(Fachrichtung::getName)
                        .collect(Collectors.toList())
                : List.of();
    }
}
