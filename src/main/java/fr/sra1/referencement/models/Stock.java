package fr.sra1.referencement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_reference", nullable = false)
    private Article article;

    private int quantity;
    
    @Column(name = "best_before")
    private LocalDate bestBefore;
    
    public Stock(Article article, int quantity) {
        if (article.getIsPerishable()) {
            throw new IllegalArgumentException("Article is perishable, best before date is required");
        }
        this.article = article;
        this.quantity = quantity;
    }
    
    public Stock(Article article, int quantity, LocalDate bestBefore) {
        if (!article.getIsPerishable()) {
            throw new IllegalArgumentException("Article is not perishable, best before date is not allowed");
        }
        this.article = article;
        this.quantity = quantity;
        this.bestBefore = bestBefore;
    }

    public int decrementStockAndRetrieveRemainingQuantityToDecrement(int quantityToDecrement){
        int remainingQuantityToDecrement = quantityToDecrement;

        if (quantity >= quantityToDecrement) {
            quantity -= quantityToDecrement;
            remainingQuantityToDecrement = 0;
        } else {
            remainingQuantityToDecrement -= quantityToDecrement;
            quantity = 0;
        }

        return remainingQuantityToDecrement;
    }
}
