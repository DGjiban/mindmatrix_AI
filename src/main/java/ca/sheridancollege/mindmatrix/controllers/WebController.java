package ca.sheridancollege.mindmatrix.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.sql.exec.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.beans.User;
import ca.sheridancollege.mindmatrix.beans.UserAnswer;
import ca.sheridancollege.mindmatrix.services.FirebaseFirestoreService;
import ca.sheridancollege.mindmatrix.services.FlashcardService;
import ca.sheridancollege.mindmatrix.services.QuizService;

@Controller
public class WebController {
	
	@Autowired
    private FlashcardService flashcardService;
	
	@Autowired
    private QuizService quizService;
	
	@Autowired
    private FirebaseFirestoreService firestoreService;

	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("message", "Welcome to MindMatrix");
		return "index";
	}
	
	@GetMapping("/login")
    public String showLoginPage() {
        return "login";  // Returns the login.html page
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
	public String showChallengePage(Model model) throws java.util.concurrent.ExecutionException {
	    try {
	        // Fetch the total number of questions
	        int questionCount = firestoreService.getTotalQuestions();
	        model.addAttribute("questionCount", questionCount);

	    } catch (ExecutionException | InterruptedException e) {
	        e.printStackTrace();
	        model.addAttribute("questionCount", "Error");
	    }
	    model.addAttribute("message", "Challenge yourself!");
	    return "challenge";  // Thymeleaf template name
	}

	
	
	    @GetMapping("/games/generate")
		public String generateGames(Model model) throws ExecutionException, InterruptedException, java.util.concurrent.ExecutionException {
		    	List<Quiz> games = quizService.getAllQuizzes();
		    	
		    	Collections.shuffle(games);
		    	
			model.addAttribute("games", games);
			model.addAttribute("game", "Welcome to MindMatrix - Game");
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
	
	@GetMapping("/quizzes/fetchAll")
	@ResponseBody
	public List<Quiz> fetchAllQuizzes() throws java.util.concurrent.ExecutionException, InterruptedException {
	    // Fetch all quizzes from Firestore and return them as JSON
	    return quizService.getAllQuizzes();
	}
	
	@GetMapping("/rankings")
    public String getRankingsPage(Model model) throws InterruptedException, ExecutionException, java.util.concurrent.ExecutionException {
        // Fetch users from Firestore
        List<User> rankedUsers = firestoreService.getRankedUsersByPoints(); // Get ranked users

        // Add the ranked users to the model
        model.addAttribute("users", rankedUsers);
        model.addAttribute("rank", "Welcome to MindMatrix - Rankings");

        return "ranking"; // Returns the ranking.html template
    }
	
	@GetMapping("/rankings/topUsers")
	@ResponseBody
	public List<User> getTopUsers() throws InterruptedException, ExecutionException, java.util.concurrent.ExecutionException {
	    List<User> rankedUsers = firestoreService.getRankedUsersByPoints();  // Fetch ranked users
	    return rankedUsers;  // Return as JSON (list of users)
	}

	
	@GetMapping("/rankings/search")
	@ResponseBody
	public List<User> searchUsersByName(@RequestParam("name") String name) throws InterruptedException, ExecutionException, java.util.concurrent.ExecutionException {
	    List<User> allUsers = firestoreService.getRankedUsersByPoints();  // Fetch all users
	    List<User> filteredUsers = allUsers.stream()
	                                       .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))  // Filter by name
	                                       .collect(Collectors.toList());
	    return filteredUsers;  // Return filtered users as JSON (list)
	}

	
	
//	@PostMapping("/quizzes/verifyAnswer")
//	@ResponseBody
//	public Map<String, Object> verifyAnswer(@RequestBody UserAnswer userAnswer) {
//	    Map<String, Object> result = new HashMap<>();
//
//	    try {
//	        // Fetch correct answer from Firestore
//	        String correctAnswer = quizService.getCorrectAnswerFromFirestore(userAnswer.getQuizId());
//
//	        if (correctAnswer != null) {
//	            // Compare the selected answer with the correct answer
//	            boolean isCorrect = userAnswer.getSelectedAnswer().equalsIgnoreCase(correctAnswer.trim());
//
//	            // Return the result
//	            result.put("isCorrect", isCorrect);
//	            result.put("correctAnswer", correctAnswer);
//	        } else {
//	            result.put("isCorrect", false);
//	            result.put("message", "No correct answer found for the quiz question.");
//	        }
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        result.put("isCorrect", false);
//	        result.put("message", "Error fetching quiz from Firestore.");
//	    }
//
//	    return result;
//	}



	
}


