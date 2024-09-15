package ca.sheridancollege.mindmatrix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.beans.QuizResult;
import ca.sheridancollege.mindmatrix.beans.UserAnswer;
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
    public String generateFlashcards(@RequestParam("prompt") String subject, 
                                     @RequestParam("number") int number, Model model) {
        model.addAttribute("message", "Welcome to MindMatrix");
        List<Flashcard> flashcards = flashcardService.generateFlashcards(subject, number);
        
        
        model.addAttribute("flashcards", flashcards);
        System.out.println(flashcards);
        return "flashcard"; // Redirecting back to index page with flashcards
    }
	
	@GetMapping("/quizzes/generate")
    public String generateQuizzes(@RequestParam("subject") String subject, 
                                  @RequestParam("number") int number, Model model) {
        List<Quiz> quizzes = quizService.getOrCreateQuizzes(subject, number);
        model.addAttribute("quizzes", quizzes);
        return "quiz"; // Or adjust based on your page structure
    }
	
	
	@PostMapping("/quizzes/verify")
    public ResponseEntity<QuizResult> verifyQuiz(@RequestBody List<UserAnswer> answers) {
        int correctCount = 0;
        
        System.out.println(answers.indexOf(0));
        
        for (UserAnswer userAnswer : answers) {
            Quiz quiz = quizService.findQuizById(userAnswer.getQuizId());
            System.out.println(quiz);
            if (quiz != null && quiz.getCorrectAnswerText().equals("Correct answer: " + userAnswer.getSelectedAnswer())) {
                System.out.println(quiz);
            	correctCount++;
            }
        }	

        QuizResult result = new QuizResult(correctCount, answers.size());
        return ResponseEntity.ok(result); // Retorna o resultado como JSON
    }
	
	 @GetMapping("/about")
	    public String aboutPage() {
	        return "about"; // Refers to src/main/resources/templates/about.html
	    }
	

	@GetMapping("/contact")
		public String contactPage() {
			return "contact"; // Refers to src/main/resources/templates/about.html
		}
	}


