package fr.sra1.referencement.repositories;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ArticleRepositoryTest {
    private final Article article1 = new Article("Reference 1", "Poire", Category.FRUIT, new ArrayList<>(), true);

    private final Article article2 = new Article("Reference 2", "Steak", Category.MEAT, new ArrayList<>(), false);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void beforeEach() {
        stockRepository.deleteAll();
        articleRepository.deleteAll();

        articleRepository.save(article1);
        articleRepository.save(article2);
    }

    @Test
    @DisplayName("Should find the article by name.")
    void testFindByCriteriaFindName() {
        List<Article> expected = List.of(article1);
        List<Article> actual = articleRepository.findByCriteria("Poire", null, null, PageRequest.of(0, 2)).getContent();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should find the article by category.")
    void testFindByCriteriaFindCategory() {
        List<Article> expected = List.of(article2);
        List<Article> actual = articleRepository.findByCriteria(null, Category.MEAT, null, PageRequest.of(0, 2)).getContent();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should find the article by reference.")
    void testFindByCriteriaFindReference() {
        List<Article> expected = List.of(article2);
        List<Article> actual = articleRepository.findByCriteria(null, null, "Reference 2", PageRequest.of(0, 2)).getContent();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should find all articles.")
    void testFindByCriteriaFindAll() {
        List<Article> expected = List.of(article1, article2);
        List<Article> actual = articleRepository.findByCriteria(null, null, null, PageRequest.of(0, 2)).getContent();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
