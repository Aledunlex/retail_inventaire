package fr.sra1.referencement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long id;

    private HashMap<Article, Integer> articles = new HashMap<>();

    private LocalDate receptionDate;

    private OrderStatus status;
}
