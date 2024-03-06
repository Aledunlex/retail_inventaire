package fr.sra1.referencement.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Redirige chaque exception non gérée vers la page d'erreur depuis n'importe quel contrôleur
    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, Model model) {
        model.addAttribute("error", exception.getMessage());

        return "error";
    }
}

