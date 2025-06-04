package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.FachrichtungDto;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.*;
import whz.project.demo.enums.Role;
import whz.project.demo.services.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final FachrichtungService fachrichtungService;
    private final ArztService arztService;
    private final LeistungenService leistungenService;
    private final MedikamentService medikamentService;
    private final BenutzerService benutzerService;
    private final PatientService patientService;
    private final ReviewService reviewService;
    private final RezeptService rezeptService;
    private final TerminService terminService;


    @GetMapping("/leistungen/form")
    public String showForm(Model model) {
        model.addAttribute("arzte", arztService.findAll());
        model.addAttribute("leistungen", leistungenService.findAll());
        return "admin/arzt_leistungen_form";
    }

    @PostMapping("/leistungen/zuweisen")
    public String assignLeistungen(@RequestParam Long arztId, @RequestParam List<Long> leistungIds) throws Exception {
        Arzt arzt = arztService.findById(arztId);

        List<Leistungen> leistungen = leistungenService.findAllByIds(leistungIds);
        arzt.setLeistungen(leistungen);
        arztService.save(arzt);

        return "redirect:/admin/leistungen/form?success";
    }

    @GetMapping("/leistungen/neu")
    public String showLeistungenForm(Model model) {
        model.addAttribute("leistung", new Leistungen());
        return "admin/leistungen_erstellen";
    }

    @PostMapping("/leistungen/neu")
    public String saveLeistung(@ModelAttribute Leistungen leistung) {
        leistungenService.save(leistung);
        return "redirect:/admin/leistungen/form?created";
    }

    @GetMapping("/medikament")
    public String listMedikament(Model model){
        model.addAttribute("medikament", medikamentService.findAll());
        return "admin/medikament";
    }

    @PostMapping("/medikament/erstellen")
    public String saveMedikament(@ModelAttribute MedikamentDto medikament) {
        medikamentService.save(medikament);
        return "redirect:/admin/medikament";

    }

    @GetMapping("/fachrictung")
    public String listFachrictungs(Model model) {
        model.addAttribute("arzts", arztService.findAll());
        model.addAttribute("fachrictungs", fachrichtungService.findAll());
        return "admin/fachrichtung";
    }

    @PostMapping("/fachrictung/erstellen")
    public String saveFachrichtung(@ModelAttribute FachrichtungDto fachrichtung){
        fachrichtungService.save(fachrichtung);
        return "redirect:/admin/fachrictung";

    }
    @PostMapping("/fachrichtung/zuweisen")
    public String fachrictungZuweisen(@RequestParam List<Long> fach_ids,Long arzt_id ) throws Exception {
        Arzt arzt = arztService.findById(arzt_id);
        List<Fachrichtung> fachrictungs = fachrichtungService.findAllByIds(fach_ids);
        arzt.setFachrichtungList(fachrictungs);
        arztService.save(arzt);
        return "redirect:/admin/fachrictung";
    }


    // XML Export functionality
    @GetMapping("/export")
    public String showExportPage(Model model) {
        return "admin/export";
    }


    @GetMapping("/export/fachrichtungen")
    public ResponseEntity<String> exportFachrichtungenToXml() {
        try {
            List<Fachrichtung> fachrichtungen = fachrichtungService.findAll();
            String xml = convertToXml(fachrichtungen, "Fachrichtungen", "Fachrichtung");
            return createXmlResponse(xml, "fachrichtungen");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/leistungen")
    public ResponseEntity<String> exportLeistungenToXml() {
        try {
            List<Leistungen> leistungen = leistungenService.findAll();
            String xml = convertToXml(leistungen, "Leistungen", "Leistung");
            return createXmlResponse(xml, "leistungen");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/medikamente")
    public ResponseEntity<String> exportMedikamenteToXml() {
        try {
            List<Medikament> medikamente = medikamentService.findAll();
            String xml = convertToXml(medikamente, "Medikamente", "Medikament");
            return createXmlResponse(xml, "medikamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/benutzer")
    public ResponseEntity<String> exportBenutzerToXml() {
        try {
            List<Benutzer> benutzer = benutzerService.findAll();
            String xml = convertToXml(benutzer, "Benutzer", "Benutzer");
            return createXmlResponse(xml, "benutzer");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/export/reviews")
    public ResponseEntity<String> exportReviewsToXml() {
        try {
            List<Review> reviews = reviewService.findAll();
            String xml = convertToXml(reviews, "Reviews", "Review");
            return createXmlResponse(xml, "reviews");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/rezepte")
    public ResponseEntity<String> exportRezepteToXml() {
        try {
            List<Rezept> rezepte = rezeptService.findAll();
            String xml = convertToXml(rezepte, "Rezepte", "Rezept");
            return createXmlResponse(xml, "rezepte");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/termine")
    public ResponseEntity<String> exportTermineToXml() {
        try {
            List<Termin> termine = terminService.findAll();
            String xml = convertToXml(termine, "Termine", "Termin");
            return createXmlResponse(xml, "termine");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private <T> String convertToXml(List<T> data, String rootElement, String itemElement) throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<").append(rootElement).append(">\n");
        xml.append("  <exportInfo>\n");
        xml.append("    <timestamp>").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</timestamp>\n");
        xml.append("    <totalRecords>").append(data.size()).append("</totalRecords>\n");
        xml.append("  </exportInfo>\n");
        xml.append("  <data>\n");

        for (T item : data) {
            xml.append("    <").append(itemElement).append(">\n");
            xml.append(objectToXml(item));
            xml.append("    </").append(itemElement).append(">\n");
        }

        xml.append("  </data>\n");
        xml.append("</").append(rootElement).append(">\n");
        return xml.toString();
    }

    private String objectToXml(Object obj) {
        StringBuilder xml = new StringBuilder();
        try {
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value != null && !field.getName().equals("serialVersionUID")) {
                    String fieldName = field.getName();
                    xml.append("      <").append(fieldName).append(">");

                    // Обработка коллекций
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        xml.append("\n");
                        for (Object listItem : list) {
                            xml.append("        <item>");
                            if (listItem != null) {
                                if (hasIdField(listItem)) {
                                    xml.append(getSimpleObjectInfo(listItem));
                                } else {
                                    xml.append(escapeXml(listItem.toString()));
                                }
                            }
                            xml.append("</item>\n");
                        }
                        xml.append("      ");

                    } else if (isSimpleType(value)) {
                        xml.append(escapeXml(value.toString()));
                    } else if (hasIdField(value)) {
                        xml.append(getSimpleObjectInfo(value));
                    } else {
                        xml.append(escapeXml(value.toString()));
                    }

                    xml.append("</").append(fieldName).append(">\n");
                }
            }
        } catch (Exception e) {
            xml.append("      <error>Error processing object: ").append(e.getMessage()).append("</error>\n");
        }
        return xml.toString();
    }

    private boolean isSimpleType(Object obj) {
        return obj instanceof String || obj instanceof Number || obj instanceof Boolean ||
                obj instanceof java.time.LocalDate || obj instanceof java.time.LocalDateTime ||
                obj.getClass().isPrimitive();
    }

    private boolean hasIdField(Object obj) {
        try {
            obj.getClass().getDeclaredField("id");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private String getSimpleObjectInfo(Object obj) {
        try {
            java.lang.reflect.Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(obj);

            String name = "";
            // Попробуем найти поле с названием
            for (String fieldName : new String[]{"name", "bezeichnung", "titel", "vorname", "nachname"}) {
                try {
                    java.lang.reflect.Field nameField = obj.getClass().getDeclaredField(fieldName);
                    nameField.setAccessible(true);
                    Object nameValue = nameField.get(obj);
                    if (nameValue != null) {
                        name = " - " + nameValue.toString();
                        break;
                    }
                } catch (NoSuchFieldException ignored) {}
            }

            return "ID: " + id + name;
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private ResponseEntity<String> createXmlResponse(String xml, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDispositionFormData("attachment",
                filename + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xml");

        return ResponseEntity.ok()
                .headers(headers)
                .body(xml);
    }


    // Замените существующие методы экспорта в AdminController

    @GetMapping("/export/arzte")
    public ResponseEntity<String> exportArzteToXml() {
        try {
            List<Arzt> arzte = arztService.findAll();
            String xml = convertArzteToXml(arzte);
            return createXmlResponse(xml, "arzte");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/patienten")
    public ResponseEntity<String> exportPatientenToXml() {
        try {
            List<Patient> patienten = patientService.findAll();
            String xml = convertPatientenToXml(patienten);
            return createXmlResponse(xml, "patienten");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Специальный метод для конвертации Arzt с данными Benutzer
    private String convertArzteToXml(List<Arzt> arzte) throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Arzte>\n");
        xml.append("  <exportInfo>\n");
        xml.append("    <timestamp>").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</timestamp>\n");
        xml.append("    <totalRecords>").append(arzte.size()).append("</totalRecords>\n");
        xml.append("  </exportInfo>\n");
        xml.append("  <data>\n");

        for (Arzt arzt : arzte) {
            xml.append("    <Arzt>\n");
            xml.append(arztToXml(arzt));
            xml.append("    </Arzt>\n");
        }

        xml.append("  </data>\n");
        xml.append("</Arzte>\n");
        return xml.toString();
    }

    // Специальный метод для конвертации Patient с данными Benutzer
    private String convertPatientenToXml(List<Patient> patienten) throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Patienten>\n");
        xml.append("  <exportInfo>\n");
        xml.append("    <timestamp>").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</timestamp>\n");
        xml.append("    <totalRecords>").append(patienten.size()).append("</totalRecords>\n");
        xml.append("  </exportInfo>\n");
        xml.append("  <data>\n");

        for (Patient patient : patienten) {
            xml.append("    <Patient>\n");
            xml.append(patientToXml(patient));
            xml.append("    </Patient>\n");
        }

        xml.append("  </data>\n");
        xml.append("</Patienten>\n");
        return xml.toString();
    }

    // Метод для конвертации Arzt в XML с включением данных Benutzer
    private String arztToXml(Arzt arzt) {
        StringBuilder xml = new StringBuilder();
        try {
            // Сначала добавляем поля самого Arzt
            xml.append(objectToXml(arzt));

            // Затем добавляем поля из связанного Benutzer
            xml.append("      <benutzerInfo>\n");
            xml.append("        <benutzerId>").append(arzt.getId()).append("</benutzerId>\n");
            xml.append("        <email>").append(escapeXml(arzt.getEmail())).append("</email>\n");
            xml.append("        <vorname>").append(escapeXml(arzt.getVorname())).append("</vorname>\n");
            xml.append("        <nachname>").append(escapeXml(arzt.getNachname())).append("</nachname>\n");
            xml.append("        <telefon>").append(escapeXml(arzt.getTelefonnummer())).append("</telefon>\n");
            xml.append("        <adresse>").append(escapeXml(arzt.getAddress())).append("</adresse>\n");
            xml.append("        <geburtsdatum>").append(arzt.getGeburtsdatum()).append("</geburtsdatum>\n");
            xml.append("        <role>").append(arzt.getRole()).append("</role>\n");
            xml.append("        <isActive>").append(arzt.isActive()).append("</isActive>\n");
            xml.append("      </benutzerInfo>\n");

        } catch (Exception e) {
            xml.append("      <error>Error processing Arzt: ").append(e.getMessage()).append("</error>\n");
        }
        return xml.toString();
    }

    // Метод для конвертации Patient в XML с включением данных Benutzer
    private String patientToXml(Patient patient) {
        StringBuilder xml = new StringBuilder();
        try {
            // Сначала добавляем поля самого Patient
            xml.append(objectToXml(patient));

            // Затем добавляем поля из связанного Benutzer
            xml.append("      <benutzerInfo>\n");

            xml.append("        <benutzerId>").append(patient.getId()).append("</benutzerId>\n");
            xml.append("        <email>").append(escapeXml(patient.getEmail())).append("</email>\n");
            xml.append("        <vorname>").append(escapeXml(patient.getVorname())).append("</vorname>\n");
            xml.append("        <nachname>").append(escapeXml(patient.getNachname())).append("</nachname>\n");
            xml.append("        <telefon>").append(escapeXml(patient.getTelefonnummer())).append("</telefon>\n");
            xml.append("        <adresse>").append(escapeXml(patient.getAddress())).append("</adresse>\n");
            xml.append("        <geburtsdatum>").append(patient.getGeburtsdatum()).append("</geburtsdatum>\n");
            xml.append("        <role>").append(patient.getRole()).append("</role>\n");
            xml.append("        <isActive>").append(patient.isActive()).append("</isActive>\n");

            xml.append("      </benutzerInfo>\n");

        } catch (Exception e) {
            xml.append("      <error>Error processing Patient: ").append(e.getMessage()).append("</error>\n");
        }
        return xml.toString();
    }




}
