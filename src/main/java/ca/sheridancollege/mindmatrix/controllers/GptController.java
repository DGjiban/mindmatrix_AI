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

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.gpt.GptRequest;
import ca.sheridancollege.mindmatrix.gpt.GptResponse;
import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;

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
}