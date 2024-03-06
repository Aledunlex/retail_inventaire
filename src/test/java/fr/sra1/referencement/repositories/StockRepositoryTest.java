package fr.sra1.referencement.repositories;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import fr.sra1.referencement.models.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockRepositoryTest {

     private final Article article = new Article("1", "Tabouret", Category.FRUIT, List.of(), false);
     private final Article article_r = new Article("2", "Poire", Category.FRUIT, List.of(), true);

    private final Stock stock1 = new Stock(1L, article, 10, null);
    private final Stock stock2 = new Stock(2L, article_r, 10, LocalDate.now().minusYears(1));
    private final Stock stock3 = new Stock(3L, article_r, 10, LocalDate.ofYearDay(9999, 1));

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void beforeEach() {
        stockRepository.deleteAll();
        articleRepository.deleteAll();

        articleRepository.save(article);
        articleRepository.save(article_r);

        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);
    }

    @Test
    void testFindByArticle() {
        List<Stock> expected = List.of(stock1);
        List<Stock> actual = stockRepository.findByArticle(article);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testFindByArticleAndBestBeforeGreaterThanEqualOrderByBestBeforeAsc() {
        List<Stock> expected = List.of(stock3);
        List<Stock> actual = stockRepository.findByArticleAndBestBeforeGreaterThanEqualOrderByBestBeforeAsc(article_r, LocalDate.now());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void testFindByBestBefore() {
        List<Stock> expected = List.of(stock2);
        List<Stock> actual = stockRepository.findByBestBefore(LocalDate.now(), PageRequest.of(0, 2)).getContent();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
