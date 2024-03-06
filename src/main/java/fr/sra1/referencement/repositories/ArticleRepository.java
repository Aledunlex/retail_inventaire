package fr.sra1.referencement.repositories;

import fr.sra1.referencement.models.Article;
import fr.sra1.referencement.models.Category;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    @Query("SELECT a FROM Article a WHERE " +
            "(:name IS NULL OR :name = '' OR a.name = :name) AND " +
            "(:category IS NULL OR a.category = :category) AND " +
            "(:reference IS NULL OR :reference = '' OR a.reference = :reference)")
    Page<Article> findByCriteria(String name, Category category, String reference, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a JOIN a.stocks s " +
            "WHERE (s.bestBefore >= :maximumDate OR s.bestBefore IS NULL) AND s.quantity > 0")
    Page<Article> findAllForClient(LocalDate maximumDate, Pageable pageable);

}
