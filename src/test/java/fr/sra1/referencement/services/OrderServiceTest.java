package fr.sra1.referencement.services;

import fr.sra1.referencement.exceptions.ArticleNotFoundException;
import fr.sra1.referencement.exceptions.InvalidOrderException;
import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.repositories.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private ArticleRepository articleRepository;

    @Test
    void decrementStocksOk() {
        Article article = Mockito.mock(Article.class);
        Mockito.when(article.getOrderableStocks()).thenReturn(10);

        String articleId = "article1";
        Mockito.when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        List<String> articleIds = List.of(articleId);
        List<Integer> quantities = List.of(5);

        orderService.decrementStocks(articleIds, quantities);

        Mockito.verify(article, Mockito.times(1)).decrementStockForOrderable(5);
        Mockito.verify(articleRepository, Mockito.times(1)).saveAll(any());
    }

    @Test
    void decrementStocksIfArticleNotFound() {
        String articleId = "unknownArticle";
        Mockito.when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        List<String> articleIds = List.of(articleId);
        List<Integer> quantities = List.of(1);

        assertThrows(ArticleNotFoundException.class, () -> orderService.decrementStocks(articleIds, quantities));
    }

    @Test
    void decrementStocksRequestedQuantityExceedsStock() {
        Article article = Mockito.mock(Article.class);
        Mockito.when(article.getOrderableStocks()).thenReturn(3);

        String articleId = "article1";
        Mockito.when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        List<String> articleIds = List.of(articleId);
        List<Integer> quantities = List.of(5);

        assertThrows(InvalidOrderException.class, () -> orderService.decrementStocks(articleIds, quantities));
    }

    @Test
    void decrementStocksErrorIfDatabaseError() {
        Article article = Mockito.mock(Article.class);
        Mockito.when(article.getOrderableStocks()).thenReturn(10);

        String articleId = "article1";
        Mockito.when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        List<String> articleIds = List.of(articleId);
        List<Integer> quantities = List.of(5);

        Mockito.doThrow(new RuntimeException("Database error")).when(articleRepository).saveAll(any());

        assertThrows(RuntimeException.class, () -> orderService.decrementStocks(articleIds, quantities));
    }

    @Test
    void decrementStocksErrorIfEnoughStocksButAreNotOrderable() {
        Article article = Mockito.mock(Article.class);
        Mockito.when(article.getStock()).thenReturn(5);
        Mockito.when(article.getOrderableStocks()).thenReturn(0);

        String articleId = "article1";
        Mockito.when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        List<String> articleIds = List.of(articleId);
        List<Integer> quantities = List.of(5);

        assertThrows(InvalidOrderException.class, () -> orderService.decrementStocks(articleIds, quantities));
    }

}
