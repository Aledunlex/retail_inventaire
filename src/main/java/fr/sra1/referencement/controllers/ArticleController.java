package fr.sra1.referencement.controllers;

import fr.sra1.referencement.exceptions.ArticleNotFoundException;
import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;


@Controller
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController extends AbstractController {
    private static final String ARTICLES_LIST_VIEW_NAME = "articles";

    private static final String MODIFY_ARTICLE_VIEW_NAME = "modif";

    private static final String CREATE_ARTICLE_VIEW_NAME = "create";

    private static final String PERISHABLES_VIEW_NAME = "perishables";

    private static final int ARTICLES_PER_PAGE = 10;

    private final ArticleRepository articleRepository;

    private final StockRepository stockRepository;

    @GetMapping
    public String retrieveArticleList(Model model, @ModelAttribute Article article,
                                      @RequestParam(required = false) String role,
                                      @RequestParam(defaultValue = "1") int pageNumber,
                                      HttpSession session, HttpServletResponse response) {
        // Pour ne pas perdre la recherche effectuée (sinon on cherche toujours name=null,category=null,reference=null)
        if (session.isNew()) {
            session.setAttribute(ARTICLE_ATTRIBUTE_NAME, article);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, ARTICLES_PER_PAGE);
        Page<Article> page;

        if ("client".equals(role)) {
            page = articleRepository.findAllForClient(LocalDate.now().plusDays(5), pageable);
        } else {
            page = articleRepository.findByCriteria(article.getName(), article.getCategory(), article.getReference(), pageable);
        }

        if (page.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new ArticleNotFoundException("Aucun article n'a été trouvé !");
        }

        model.addAttribute(CURRENT_PAGE_ATTRIBUTE_NAME, pageNumber);
        model.addAttribute(TOTAL_PAGES_ATTRIBUTE_NAME, page.getTotalPages());
        model.addAttribute(TOTAL_ITEMS_ATTRIBUTE_NAME, page.getTotalElements());
        model.addAttribute(ARTICLES_ATTRIBUTE_NAME, page.getContent());

        return ARTICLES_LIST_VIEW_NAME;
    }

    @GetMapping("/{reference}")
    public String toPageArticle(Model model, @PathVariable String reference, HttpServletResponse response) {
        model.addAttribute(ARTICLE_ATTRIBUTE_NAME, articleRepository.findById(reference).orElseThrow(() -> {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ArticleNotFoundException(reference);
        }));
        model.addAttribute(CATEGORIES_ATTRIBUTE_NAME, Category.values());

        return MODIFY_ARTICLE_VIEW_NAME;
    }

    @PostMapping("/{reference}")
    public String modifyArticle(Model model, @ModelAttribute Article article, @PathVariable String reference,
                                HttpServletResponse response) {
        model.addAttribute(CATEGORIES_ATTRIBUTE_NAME, Category.values());
        if ("".equals(article.getName()) || "".equals(article.getReference())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "Tous les champs doivent être renseignés !");
            return MODIFY_ARTICLE_VIEW_NAME;
        }
        article.setReference(reference);
        articleRepository.save(article);

        return MODIFY_ARTICLE_VIEW_NAME;
    }

    @GetMapping("/create")
    public String toCreateArticle(Model model) {
        model.addAttribute(ARTICLE_ATTRIBUTE_NAME, new Article());
        model.addAttribute(CATEGORIES_ATTRIBUTE_NAME, Category.values());

        return CREATE_ARTICLE_VIEW_NAME;
    }

    @PostMapping("/create")
    public String createArticle(@ModelAttribute Article article, Model model, HttpServletResponse response) {
        model.addAttribute(CATEGORIES_ATTRIBUTE_NAME, Category.values());
        if ("".equals(article.getName()) || "".equals(article.getReference())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "Tous les champs doivent être renseignés !");
            return CREATE_ARTICLE_VIEW_NAME;
        }
        articleRepository.save(article);

        model.addAttribute(ARTICLE_ATTRIBUTE_NAME, new Article());
        model.addAttribute(EXISTING_ARTICLE_ATTRIBUTE_NAME, article.getName());

        return CREATE_ARTICLE_VIEW_NAME;
    }

    @GetMapping("/perishables")
    public String retrieveExpiredArticles(Model model, @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, ARTICLES_PER_PAGE);
        Page<Stock> page = stockRepository.findByBestBefore(LocalDate.now(), pageable);

        model.addAttribute(CURRENT_PAGE_ATTRIBUTE_NAME, pageNumber);
        model.addAttribute(TOTAL_PAGES_ATTRIBUTE_NAME, page.getTotalPages());
        model.addAttribute(TOTAL_ITEMS_ATTRIBUTE_NAME, page.getTotalElements());
        model.addAttribute(STOCKS_ATTRIBUTE_NAME, page.getContent());

        return PERISHABLES_VIEW_NAME;
    }

}
