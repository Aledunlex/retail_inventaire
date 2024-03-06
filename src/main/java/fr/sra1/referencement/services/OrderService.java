package fr.sra1.referencement.services;

import fr.sra1.referencement.exceptions.ArticleNotFoundException;
import fr.sra1.referencement.exceptions.InvalidOrderException;
import fr.sra1.referencement.exceptions.OrderAlreadyAcceptedException;
import fr.sra1.referencement.exceptions.OrderTooOldToBeReturned;
import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Order;
import fr.sra1.referencement.models.OrderStatus;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrderService {

    private static final int MAX_RETURN_DELAY = 7;
    private ArticleRepository articleRepository;

    private StockRepository stockRepository;

    public void decrementStocks(List<String> articleIds, List<Integer> quantities) {
        Map<Article, Integer> order = validateAndFetchArticles(articleIds, quantities);

        if (isCommandValid(order)) {
            for (Map.Entry<Article, Integer> entry : order.entrySet()) {
                Article article = entry.getKey();
                Integer quantityToDecrement = entry.getValue();

                article.decrementStockForOrderable(quantityToDecrement);
            }
            articleRepository.saveAll(order.keySet());
        } else {
            throw new InvalidOrderException();
        }
    }

    public Map<Article, Integer> validateAndFetchArticles(List<String> articleIds, List<Integer> quantities) throws IllegalArgumentException {
        Map<Article, Integer> order = new HashMap<>();

        for (int k = 0; k < articleIds.size(); k++) {
            String articleReference = articleIds.get(k);
            Integer quantity = quantities.get(k);

            if (quantity > 0) {
                Article article = articleRepository.findById(articleReference).orElseThrow(() -> new ArticleNotFoundException(articleReference));
                order.put(article, quantity);
            }
        }

        return order;
    }

    private boolean isCommandValid(Map<Article, Integer> order) {
        boolean isCommandValid = true;

        for (Map.Entry<Article, Integer> entry : order.entrySet()) {
            Article article = entry.getKey();
            Integer quantityToDecrement = entry.getValue();

            isCommandValid &= article.getOrderableStocks() >= quantityToDecrement;
        }

        return isCommandValid;
    }

    public void addStockToReturnedArticles(Order order) throws OrderTooOldToBeReturned, OrderAlreadyAcceptedException {
        LocalDate todayMinusMaxReturnDelay = LocalDate.now().minusDays(MAX_RETURN_DELAY);

        if (order.getReceptionDate().isBefore(todayMinusMaxReturnDelay)) {
            throw new OrderTooOldToBeReturned();
        }

        if (order.getStatus() == OrderStatus.ACCEPTED) {
            throw new OrderAlreadyAcceptedException();
        }

        List<Article> articlesToReturn = new ArrayList<>();
        for (Map.Entry<Article, Integer> entry : order.getArticles().entrySet()) {
            Article article = entry.getKey();
            Integer quantityToReturn = entry.getValue();

            // Perishable articles are destroyed
            if (!article.getIsPerishable())  {
                Stock stock = new Stock(article, quantityToReturn);
                stockRepository.save(stock);
                article.getStocks().add(stock);
                articlesToReturn.add(article);
            }
        }
        articleRepository.saveAll(articlesToReturn);
    }

}
