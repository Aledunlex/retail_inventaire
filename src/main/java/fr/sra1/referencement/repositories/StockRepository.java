package fr.sra1.referencement.repositories;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByArticle(Article article);

    List<Stock> findByArticleAndBestBeforeGreaterThanEqualOrderByBestBeforeAsc(Article article, LocalDate date);

    @Query("SELECT s FROM Stock s WHERE s.article.isPerishable = true AND s.bestBefore < :bestBefore")
    Page<Stock> findByBestBefore(LocalDate bestBefore, Pageable pageable);

}
