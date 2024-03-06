package fr.sra1.referencement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController extends AbstractController {
    @GetMapping
    public String redirectToArticles() {
        return "redirect:search";
    }
}
