package fr.sra1.referencement.controllers;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;
import fr.sra1.referencement.repositories.ArticleRepository;
import fr.sra1.referencement.repositories.StockRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private final String ARTICLES_LIST_PATH = "/articles";

    private final String MODIF_VIEW_NAME = "modif";

    private final String CREATE_VIEW_NAME = "create";

    private static final String ARTICLES_LIST_VIEW_NAME = "articles";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private StockRepository stockRepository;

    private Article getArticle() {
        return new Article("name", "reference", Category.MEAT, new ArrayList<>(), false);
    }

    @Test
    void testRetrieveArticleList() throws Exception {
        Article article = getArticle();
        Page<Article> page = Mockito.mock(Page.class);

        Mockito.when(articleRepository.findByCriteria(eq(article.getName()), eq(article.getCategory()), eq(article.getReference()), any()))
                .thenReturn(page);
        Mockito.when(page.getTotalPages()).thenReturn(1);

        mockMvc.perform(get(ARTICLES_LIST_PATH).flashAttr("article", article))
                .andExpect(status().isOk())
                .andExpect(view().name(ARTICLES_LIST_VIEW_NAME));

        Mockito.verify(articleRepository).findByCriteria(eq(article.getName()), eq(article.getCategory()), eq(article.getReference()), any());
    }

    @Test
    void testToPageArticle() throws Exception {
        Article article = getArticle();

        Mockito.when(articleRepository.findById(article.getReference())).thenReturn(Optional.of(article));

        mockMvc.perform(get(ARTICLES_LIST_PATH + "/{reference}", article.getReference()).flashAttr("article", article))
                .andExpect(status().isOk())
                .andExpect(view().name(MODIF_VIEW_NAME));

        Mockito.verify(articleRepository).findById(article.getReference());
    }

    @Test
    void testToCreateArticle() throws Exception {
        mockMvc.perform(get(ARTICLES_LIST_PATH + "/create"))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_VIEW_NAME));
    }

}
