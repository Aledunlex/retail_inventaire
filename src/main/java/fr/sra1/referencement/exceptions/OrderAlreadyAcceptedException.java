package fr.sra1.referencement.exceptions;

public class OrderAlreadyAcceptedException extends RuntimeException {
    public OrderAlreadyAcceptedException() {
        super("La commande a déjà été acceptée, et ne peut donc plus être retournée.");
    }
}
