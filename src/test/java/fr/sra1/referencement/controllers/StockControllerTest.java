package fr.sra1.referencement.controllers;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.models.StockWrapper;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(StockController.class)
class StockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("Should retrieve the create stock page.")
    void testToCreateStock() throws Exception {
        mockMvc.perform(get("/stock/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("new-stock"));
    }

    @Test
    @DisplayName("Should retrieve the modify stock page.")
    void testRetrieveModifyStockPage() throws Exception {
        Article article = new Article("ref", "name", Category.MEAT, new ArrayList<>(), true);

        List<Stock> stocks = new ArrayList<>();
        stocks.add(new Stock(article, 1, LocalDate.parse("2021-01-01")));

        article.setStocks(stocks);

        Mockito.when(articleRepository.findById(article.getReference())).thenReturn(Optional.of(article));
        Mockito.when(stockRepository.findByArticle(article)).thenReturn(stocks);

        mockMvc.perform(get("/stock/ref")
                        .flashAttr("article", article)
                        .flashAttr("stockWrapper", new StockWrapper(stocks)))
                .andExpect(status().isOk())
                .andExpect(view().name("modify-stocks"));
    }
}
