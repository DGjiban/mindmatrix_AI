package ca.sheridancollege.mindmatrix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.services.GptService;

@RestController
@RequestMapping("/chat")
public class GptController {

	@Autowired
    private GptService gptService;

	@GetMapping("/flash")
	public ResponseEntity<?> generateFlashcards(@RequestParam("subject") String subject, @RequestParam("number") int number) {
        try {
            List<Flashcard> flashcards = gptService.generateFlashcards(subject, number);
            return ResponseEntity.ok(flashcards); // Return the list of flashcards
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate flashcards: " + e.getMessage());
        }
    }

//	@GetMapping("/quiz")
//    public String generateQuiz(@RequestParam("subject") String subject, @RequestParam("number") Integer number) {
//        List<Quiz> quizzes = new ArrayList<>();
//        
//        for (int i = 0; i < number; i++) {
//         
//            String prompt = "Generate a multiple choice question on " + subject + ", you must identify the question from the answers using 'Question: '"
//            		+ "and the correct answer must be identified by 'Correct Answer: ";
//            
//           
//            GptRequest request = new GptRequest(model, prompt, 300);
//            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);
//
//            if (responseEntity.getBody().getChoices() != null && !responseEntity.getBody().getChoices().isEmpty()) {
//                String response = responseEntity.getBody().getChoices().get(0).getMessage().getContent();
//                
//                Quiz quiz = organizeQuizQuestion(response, subject);
//                
//                System.out.println(quiz);
//                
//                if (quiz != null) {
//                    quiz.setSubject(subject);
//                    
//                    System.out.println(quiz);
// 
//                    quizRepository.save(quiz);
//                    quizzes.add(quiz);
//                } else {
//                    return "Invalid response format.";
//                }
//            } else {
//                return "Failed to get response.";
//            }
//        }
//        return "Quiz generated successfully.";
//    }
//    
//    
//    private Quiz organizeQuizQuestion(String response, String subject) {
//        
//       //System.out.println("Full response from AI: " + response);
//
//        String[] lines = response.split("\n");
//        String question = null;
//        List<String> answers = new ArrayList<>();
//        String correctAnswer = null;
//
//        for (String line : lines) {
//            
//        	//System.out.println("Processing line: " + line);
//
//            if (line.startsWith("Q: ") || line.startsWith("Question: ")) {
//            	
//                question = line.replace("Q: ", "").replace("Question: ", "").trim();
//                
//            } else if (line.matches("^[A-D]\\) .*")) {
//            	
//                answers.add(line.substring(line.indexOf(") ") + 2).trim());
//                
//            } else if (line.startsWith("Correct Answer: ") || line.startsWith("Answer: ")) {
//                
//            	correctAnswer = line.substring(line.indexOf(": ") + 2).trim();
//            }
//        }
// 
//        /*System.out.println("Question: " + question);
//        System.out.println("Answers: " + answers);
//        System.out.println("Correct Answer is: " + correctAnswer);*/
//
//        if (question != null && !answers.isEmpty() && correctAnswer != null) {
//           
//            return new Quiz(null, subject, question, answers, correctAnswer);
//        } else {
//            System.out.println("Failed to organize question and answers properly.");
//            return null;
//        }
//    }
}
