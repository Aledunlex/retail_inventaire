package fr.sra1.referencement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    private String reference;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "article")
    private List<Stock> stocks = new ArrayList<>();

    @Column(name = "is_perishable")
    private boolean isPerishable;

    public int getStock() {
        return stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }
    public boolean getIsPerishable() {
        return isPerishable;
    }

    public void setIsPerishable(boolean isPerishable) {
        this.isPerishable = isPerishable;
    }

    public int getOrderableStocks() {
        // Doesn't count stocks with a best before date in the past
        return stocks.stream()
                .filter(stock -> stock.getBestBefore() == null || stock.getBestBefore().isAfter(LocalDate.now().plusDays(5)))
                .mapToInt(Stock::getQuantity)
                .sum();
    }

    public List<Stock> getAllValidDates() {
        return stocks.stream()
                .filter(stock -> stock.getBestBefore() != null && stock.getBestBefore().isAfter(java.time.LocalDate.now()))
                .toList();
    }

    public void decrementStockForOrderable(int quantityToDecrement) {
        List<Stock> orderableStocks = stocks.stream()
                .filter(stock -> stock.getBestBefore() == null || stock.getBestBefore().isAfter(LocalDate.now().plusDays(5)))
                .collect(Collectors.toList());

        while (quantityToDecrement > 0) {
            Stock stock = orderableStocks.get(0);

            quantityToDecrement = stock.decrementStockAndRetrieveRemainingQuantityToDecrement(quantityToDecrement);

            if (stock.getQuantity() <= 0) {
                stocks.remove(stock);
            }

            orderableStocks.remove(stock);
        }
    }
}
