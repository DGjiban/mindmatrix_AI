package ca.sheridancollege.mindmatrix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
//import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;
import ca.sheridancollege.mindmatrix.services.FlashcardService;
import ca.sheridancollege.mindmatrix.services.QuizService;

@Controller
public class WebController {
	
	@Autowired
    private FlashcardService flashcardService;
	
	@Autowired
    private QuizService quizService;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "Welcome to MindMatrix");
		return "index";
	}
	
	@GetMapping("/flashcards/generate")
    public String generateFlashcards(@RequestParam("prompt") String subject, @RequestParam("number") int number, Model model) {
       
		model.addAttribute("message", "Welcome to MindMatrix");
        
		List<Flashcard> flashcards = flashcardService.getOrCreateFlashcards(subject, number);
        
		model.addAttribute("flashcards", flashcards);
        
		return "index"; // Redirecting back to index page with flashcards
    }
	
	@GetMapping("/quizzes/generate")
    public String generateQuizzes(@RequestParam("subject") String subject, @RequestParam("number") int number, Model model) {
		
        List<Quiz> quizzes = quizService.getOrCreateQuizzes(subject, number);
        
        model.addAttribute("quizzes", quizzes);
        
        return "index"; // Or adjust based on your page structure
    }
}
