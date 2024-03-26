package ca.sheridancollege.mindmatrix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
//import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;
import ca.sheridancollege.mindmatrix.services.FlashcardService;

@Controller
public class WebController {
	
	@Autowired
    private FlashcardService flashcardService;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "Welcome to MindMatrix");
		return "index";
	}
	
	@GetMapping("/flashcards/generate")
    public String generateFlashcards(@RequestParam("prompt") String subject, 
                                     @RequestParam("number") int number, Model model) {
        model.addAttribute("message", "Welcome to MindMatrix");
        List<Flashcard> flashcards = flashcardService.getOrCreateFlashcards(subject, number);
        model.addAttribute("flashcards", flashcards);
        return "index"; // Redirecting back to index page with flashcards
    }
	
//	@GetMapping("/flashcards")
//	public String flashcards(Model model) {
//		Iterable<Flashcard> flashcards = flashCardRepository.findAll();
//		model.addAttribute("welcome", "This is Flashcard");
//        model.addAttribute("flashcards", flashcards);
//        return "index";
//	}
}
