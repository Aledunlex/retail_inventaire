package fr.sra1.referencement.controllers;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchController extends AbstractController {
    private static final String RESEARCH_VIEW_NAME = "research";

    @GetMapping
    public String retrieveSearchPage(Model model, HttpSession session) {
        session.invalidate();
        model.addAttribute(ARTICLE_ATTRIBUTE_NAME, new Article());
        model.addAttribute(CATEGORIES_ATTRIBUTE_NAME, Category.values());

        return RESEARCH_VIEW_NAME;
    }
}
