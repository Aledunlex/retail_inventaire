package fr.sra1.referencement.exceptions;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException() {
        super("Stock insuffisant pour assurer cette commande !");
    }
}
