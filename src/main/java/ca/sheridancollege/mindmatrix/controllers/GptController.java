package ca.sheridancollege.mindmatrix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.services.FlashcardService;
import ca.sheridancollege.mindmatrix.services.QuizService;

@RestController
@RequestMapping("/chat")
public class GptController {

	@Autowired
	private FlashcardService flashcardService;
	
	@Autowired
	private QuizService quizService;
	
	@GetMapping("/flash")
	public ResponseEntity<?> generateFlashcards(@RequestParam("subject") String subject, @RequestParam("number") int number) {
        try {
            List<Flashcard> flashcards = flashcardService.generateFlashcards(subject, number);
            return ResponseEntity.ok(flashcards); // Return the list of flashcards
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate flashcards: " + e.getMessage());
        }
    }
	
	// Endpoint to generate quizzes
    @GetMapping("/quiz")
    public ResponseEntity<?> generateQuizzes(@RequestParam("subject") String subject, @RequestParam("number") int number) {
        try {
            List<Quiz> quizzes = quizService.generateQuizzes(subject, number);
            return ResponseEntity.ok(quizzes); // Return the list of quizzes
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate quizzes: " + e.getMessage());
        }
    }
    
    @GetMapping("/game")
    public ResponseEntity<?> generateGames(@RequestParam("subject") String subject, @RequestParam("number") int number) {
        try {
            List<Quiz> games = quizService.generateQuizzes(subject, number);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate games: " + e.getMessage());
        }
    }

}
