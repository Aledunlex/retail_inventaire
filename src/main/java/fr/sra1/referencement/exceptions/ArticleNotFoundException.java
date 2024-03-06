package fr.sra1.referencement.exceptions;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String reference) {
        super(String.format("The article %s doesn't exist!", reference));
    }
}
