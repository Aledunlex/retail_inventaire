package fr.sra1.referencement.controllers;

import fr.sra1.referencement.exceptions.InvalidOrderException;
import fr.sra1.referencement.exceptions.OrderAlreadyAcceptedException;
import fr.sra1.referencement.exceptions.OrderTooOldToBeReturned;
import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Order;
import fr.sra1.referencement.models.OrderStatus;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import fr.sra1.referencement.services.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mock-order")
@AllArgsConstructor
public class OrderController extends AbstractController {
    private static final String MOCK_ORDER_VIEW_NAME = "mock-order";

    private static final String RETURN_ORDER_VIEW_NAME = "return-order";


    private final StockRepository stockRepository;


    private final ArticleRepository articleRepository;


    private final OrderService orderService;

    @GetMapping
    public String showOrderForm(Model model, HttpServletResponse response) {
        // Récupération des 4 premiers stocks dans la base de données (de test) pour l'envoyer au panier mocké
        List<Stock> stocks = stockRepository.findAll().subList(0, 4);
        List<Article> articles = stocks.stream().map(Stock::getArticle).distinct().toList();

        // Erreur si les 3 articles et leur stock ne sont pas déjà en BDD, voir data.sql
        Map<Article, Integer> order = new HashMap<>();
        order.put(articles.get(0), 0); // 1 stock valide correspond
        order.put(articles.get(1), 4); // 2 stocks correspondent à cet article, dont un invalide
        order.put(articles.get(2), 0); // 1 stock périmé correspond

        model.addAttribute(ARTICLES_ATTRIBUTE_NAME, order);

        return MOCK_ORDER_VIEW_NAME;
    }

    @PostMapping("/validateOrder")
    public String validateOrder(@RequestParam List<String> articleIds, @RequestParam List<Integer> quantities,
                                RedirectAttributes redirectAttributes, HttpServletResponse response) {
        if (articleIds == null || quantities == null || articleIds.size() != quantities.size()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE_NAME, "Commande est invalide.");
            return "redirect:/" + MOCK_ORDER_VIEW_NAME;
        }

        try {
            orderService.decrementStocks(articleIds, quantities);
            redirectAttributes.addFlashAttribute("success", "La commande a été validée.");
        } catch (InvalidOrderException invalidOrderException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE_NAME, invalidOrderException.getMessage());
        }

        return "redirect:/" + MOCK_ORDER_VIEW_NAME;
    }

    @GetMapping("/{id}")
    public String showReturnOrderForm(Model model, @PathVariable Long id) {
        List<Article> articles = articleRepository.findAll();

        // Mock un retour de commande
        HashMap<Article, Integer> items = new HashMap<>();
        items.put(articles.get(0), 5);
        items.put(articles.get(1), 2);
        items.put(articles.get(2), 8);

        Order order = new Order(id, items, LocalDate.now(), OrderStatus.RETURNED);
        if (id == 1L)
            order.setStatus(OrderStatus.ACCEPTED);
        else if (id == 2L)
            order.setReceptionDate(LocalDate.now().minusDays(50));

        model.addAttribute(ORDER_ATTRIBUTE_NAME, order);

        return RETURN_ORDER_VIEW_NAME;
    }

    @DeleteMapping("/{id}")
    public String deleteOrderAndRestock(Model model, @PathVariable Long id, @RequestParam List<String> articleIds, @RequestParam List<Integer> quantities, @RequestParam LocalDate date, @RequestParam OrderStatus status) {
        Order order = new Order(id, (HashMap<Article, Integer>) orderService.validateAndFetchArticles(articleIds, quantities), date, status);
        try {
            orderService.addStockToReturnedArticles(order);
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "La commande " + id + " a été supprimée et les stocks ont été réapprovisionnés.");
        } catch (OrderTooOldToBeReturned | OrderAlreadyAcceptedException e) {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, e.getMessage());
        }

        model.addAttribute(ORDER_ATTRIBUTE_NAME, order);
        return RETURN_ORDER_VIEW_NAME;
    }
}
