package ca.sheridancollege.mindmatrix.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	

	    @GetMapping("/challenge")
	    public String showChallengePage(Model model) {
	        model.addAttribute("message", "Challenge Yourself!");
	        return "challenge"; // The name of your Thymeleaf template file without the extension
	    }
	

	
	    @GetMapping("/games/generate")
		public String generateGames(@RequestParam("subject") String subject, 
		                            @RequestParam("number") int number, Model model) {
		    List<Quiz> games = quizService.getOrCreateQuizzes(subject, number);
		    model.addAttribute("games", games);
		    return "game"; // This should match "game.html"
		}
	
	
	@PostMapping("/quizzes/verify")
	public ResponseEntity<Map<String, Object>> verifyQuiz(@RequestBody List<UserAnswer> answers) {
	    List<Map<String, String>> results = new ArrayList<>();
	    int correctCount = 0;

	    for (UserAnswer userAnswer : answers) {
	        Quiz quiz = quizService.findQuizById(userAnswer.getQuizId());
	        String correctAnswer = quiz.getCorrectAnswerText().replace("Correct answer: ", "").trim();

	        Map<String, String> result = new HashMap<>();
	        result.put("question", quiz.getQuestion());
	        result.put("selectedAnswer", userAnswer.getSelectedAnswer());
	        result.put("correctAnswer", correctAnswer);

	        if (userAnswer.getSelectedAnswer().equals(correctAnswer)) {
	            correctCount++;
	        }

	        results.add(result);
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("correctCount", correctCount);
	    response.put("total", answers.size());
	    response.put("answers", results);

	    return ResponseEntity.ok(response);
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


