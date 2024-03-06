package fr.sra1.referencement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class StockWrapper {
    private List<Stock> stocks;
}
