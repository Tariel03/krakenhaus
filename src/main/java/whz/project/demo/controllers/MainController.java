package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import whz.project.demo.dto.ArztDTO;
import whz.project.demo.services.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class MainController {
    private final ArztService arztService;

    @GetMapping("/main")
    public String index(Model model) {
        List<ArztDTO> arztDTOList = arztService.findAll().stream()
                .map(ArztDTO::new)
                .toList();

        model.addAttribute("arzts", arztDTOList);
        model.addAttribute("arztService", arztService);
        return "home/main";
    }











}