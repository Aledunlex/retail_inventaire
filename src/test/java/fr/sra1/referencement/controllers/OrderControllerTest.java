package fr.sra1.referencement.controllers;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import fr.sra1.referencement.models.Stock;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import fr.sra1.referencement.services.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockRepository stockRepository;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("Should retrieve the mock order form.")
    void testShowOrderForm() throws Exception {
        Article article1 = new Article("name1", "reference1", Category.MEAT, new ArrayList<>(), false);
        Article article2 = new Article("name2", "reference2", Category.MEAT, new ArrayList<>(), false);
        Article article3 = new Article("name3", "reference3", Category.MEAT, new ArrayList<>(), false);
        Stock stock1 = new Stock(article1, 10);
        Stock stock2 = new Stock(article1, 10);
        Stock stock3 = new Stock(article2, 10);
        Stock stock4 = new Stock(article3, 10);

        Mockito.when(stockRepository.findAll()).thenReturn(List.of(stock1, stock2, stock3, stock4));

        mockMvc.perform(get("/mock-order").flashAttr("articles", List.of(article1, article2, article3)))
                .andExpect(status().isOk())
                .andExpect(view().name("mock-order"));
    }
}
