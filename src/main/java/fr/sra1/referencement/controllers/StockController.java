package fr.sra1.referencement.controllers;

import fr.sra1.referencement.exceptions.ArticleNotFoundException;
import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.models.StockWrapper;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/stock")
@AllArgsConstructor
public class StockController extends AbstractController {
    private static final String CREATE_STOCK_VIEW_NAME = "new-stock";

    private static final String MODIFY_STOCK_VIEW_NAME = "modify-stocks";

    private final ArticleRepository articleRepository;

    private final StockRepository stockRepository;

    @GetMapping("/create")
    public String toCreateStock(Model model) {
        Stock newStock = new Stock();
        newStock.setArticle(new Article());

        model.addAttribute(STOCK_ATTRIBUTE_NAME, newStock);
        model.addAttribute(REFERENCE_ATTRIBUTE_NAME, "");

        return CREATE_STOCK_VIEW_NAME;
    }

    @PostMapping("/create")
    public String createStock(@ModelAttribute Stock stock, Model model, HttpServletResponse response) {
        if (stock.getArticle() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "La référence donnée n'est associée à aucun article !");
            return CREATE_STOCK_VIEW_NAME;
        }
        if (stock.getBestBefore() == null || stock.getQuantity() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "Tous les champs doivent être renseignés ! (quantité positive seulement)");
            return CREATE_STOCK_VIEW_NAME;
        }
        if (!stock.getArticle().getIsPerishable()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "L'article référencé n'est pas périssable !");
            return CREATE_STOCK_VIEW_NAME;
        }
        if (stock.getBestBefore().isBefore(LocalDate.now().plusDays(5))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "La date de péremption renseignée est incorrect ! La date doit être après le " + LocalDate.now().plusDays(5) + ".");
            return CREATE_STOCK_VIEW_NAME;
        }

        stockRepository.save(stock);

        model.addAttribute(STOCK_ATTRIBUTE_NAME, new Stock());
        model.addAttribute(EXISTING_STOCK_ATTRIBUTE_NAME, stock.getId());

        return CREATE_STOCK_VIEW_NAME;
    }

    @GetMapping("/{articleReference}")
    public String retrieveModifyStockPage(
            @PathVariable String articleReference,
            @RequestParam(required = false) String error,
            Model model,
            HttpServletResponse response
    ) {
        if ("date".equals(error)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "La date de péremption renseignée est incorrect ! La date doit être après le " + LocalDate.now().plusDays(5) + ".");
        }
        if ("quantity".equals(error)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "La quantité renseignée doit être positive !");

        }
        Article article = articleRepository.findById(articleReference).orElseThrow(() -> {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ArticleNotFoundException(articleReference);
        });
        model.addAttribute(ARTICLE_ATTRIBUTE_NAME, article);
        model.addAttribute(STOCK_WRAPPER_ATTRIBUTE_NAME, new StockWrapper(stockRepository.findByArticle(article)));

        return MODIFY_STOCK_VIEW_NAME;
    }

    @PostMapping("/{articleReference}/new")
    public String addStock(@PathVariable String articleReference, HttpServletResponse response) {
        Article article = articleRepository.findById(articleReference).orElseThrow(() -> {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new ArticleNotFoundException(articleReference);
        });
        if (article.getIsPerishable()) {
            stockRepository.save(new Stock(article, 0, LocalDate.now().plusDays(5)));
        } else {
            stockRepository.save(new Stock(article, 0));
        }

        return redirectToStock(articleReference);
    }

    @PostMapping("/{articleReference}")
    public String modifyStock(@PathVariable String articleReference, @ModelAttribute StockWrapper stockWrapper, Model model, HttpServletResponse response) {
        for (Stock stock : stockWrapper.getStocks()) {
            if (stock.getBestBefore() != null && stock.getBestBefore().isBefore(LocalDate.now().plusDays(5))) {
                return redirectToStock(articleReference) + "?error=date";
            }
            if (stock.getQuantity() <= 0) {
                return redirectToStock(articleReference) + "?error=quantity";
            }
        }
        stockRepository.saveAll(stockWrapper.getStocks());

        return redirectToStock(articleReference);
    }

    @DeleteMapping("/{id}")
    public String deleteArticle(@PathVariable Long id) {
        stockRepository.deleteById(id);
        return "redirect:/articles/perishables";
    }

    private String redirectToStock(String articleReference) {
        return "redirect:/stock/" + articleReference;
    }
}
