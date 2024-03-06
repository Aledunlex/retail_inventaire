package fr.sra1.referencement.exceptions;

public class OrderTooOldToBeReturned extends RuntimeException {
    public OrderTooOldToBeReturned() {
        super("The order is too old to be returned.");
    }
}
