package ca.sheridancollege.mindmatrix.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.gpt.GptRequest;
import ca.sheridancollege.mindmatrix.gpt.GptResponse;
import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;
import ca.sheridancollege.mindmatrix.repositories.QuizRepository;

@RestController
@RequestMapping("/chat")
public class GptController {
    
    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;
    
    @Autowired
    private RestTemplate template;
    
    @Autowired
    private FlashCardRepository flashcardRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @GetMapping("/flash")
    public String generateFlashcards(@RequestParam("prompt") String prompt, @RequestParam("number") Integer number) {
        List<Flashcard> flashcards = new ArrayList<>();
                
        for (int i = 0; i < number; i++) {
            String data = "always use the format Q: and A: and with short answer and question, " + prompt;
            GptRequest request = new GptRequest(model, data);
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody().getChoices() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                GptResponse.Message message = responseEntity.getBody().getChoices().get(0).getMessage();

                Flashcard card = new Flashcard();
                
                card.setSubject(prompt);
              
                if (message != null && message.getContent() != null) {
                    String resp = message.getContent();
                    String[] lines = resp.split("\n");

                    for (String line : lines) {
                        if (line.startsWith("Q:")) {
                            card.setQuestion(line.replace("Q: ", "").trim());
                        } else if (line.startsWith("A:")) {
                            card.setAnswer(line.replace("A: ", "").trim());
                        }
                    }

                    if (card.getQuestion() != null && card.getAnswer() != null) {
                        
                        if (flashcardRepository.findByQuestion(card.getQuestion()) == null 
                        		|| flashcardRepository.findByAnswer(card.getAnswer()) == null) {
                            
                        	flashcardRepository.save(card);
                            flashcards.add(card);
                        } else {
                            i--; 
                        }
                    } else {
                        return "Invalid response format.";
                    }
                }
            } else {
                return "Failed to get response.";
            }
        }

        return "Flashcards generated successfully.";
    }  
    
    
    @GetMapping("/quiz")
    public String generateQuiz(@RequestParam("subject") String subject, @RequestParam("number") Integer number) {
        List<Quiz> quizzes = new ArrayList<>();
        
        for (int i = 0; i < number; i++) {
         
            String prompt = "Generate a quiz question and multiple-choice answers on " + subject;
            
           
            GptRequest request = new GptRequest(model, prompt);
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody().getChoices() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                String response = responseEntity.getBody().getChoices().get(0).getMessage().getContent();
                Quiz quiz = organizeFlashQuestion(response); 
                
                if (quiz != null) {
                    quiz.setSubject(subject);
 
                    quizRepository.save(quiz);
                    quizzes.add(quiz);
                } else {
                    return "Invalid response format.";
                }
            } else {
                return "Failed to get response.";
            }
        }
        return "Quiz generated successfully.";
    }
    
    
    private Quiz organizeFlashQuestion(String response) {
		String[] lines = response.split("\n");
		String question = null;
		String subject = null;
		List<String> answers = new ArrayList<>();
		int correctAnswerIndex = -1;
		    
		for (String line : lines) {
			if (line.startsWith("Q:")) {
				question = line.substring(2).trim();
			} else if (line.matches("^[A-Z]:.*")) {
				answers.add(line.substring(2).trim());
		    } else if (line.startsWith("Correct Answer:")) {
		    	String correctAnswerMark = line.substring("Correct Answer:".length()).trim();
		    	correctAnswerIndex = "ABC".indexOf(correctAnswerMark);
		    }
		}

		if (question != null && !answers.isEmpty() && correctAnswerIndex != -1) {
			return new Quiz(null, subject, question, answers, correctAnswerIndex);
		} else {
			return null;
		}
	}

    
        
}