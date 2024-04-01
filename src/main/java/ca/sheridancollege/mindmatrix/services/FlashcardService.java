package ca.sheridancollege.mindmatrix.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.gpt.GptRequest;
import ca.sheridancollege.mindmatrix.gpt.GptResponse;
import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;

@Service
public class FlashcardService {
	@Autowired
    private FlashCardRepository flashCardRepository;
	
	@Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    public List<Flashcard> getOrCreateFlashcards(String subject, int number) {
        List<Flashcard> flashcards = flashCardRepository.findBySubject(subject);

        if (flashcards.isEmpty()) {
            flashcards = generateFlashcards(subject, number);
            // Assuming generateFlashcards now saves the flashcards to the database
            // and returns the list of generated flashcards
        } else if (flashcards.size() > number) {
            flashcards = flashcards.subList(0, number);
        }

        return flashcards;
    }
    
    // Method to generate flashcards
    public List<Flashcard> generateFlashcards(String subject, int number) {
        List<Flashcard> flashcards = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            String data = "always use the format Q: and A: and with short answer and question, " + subject;
            GptRequest request = new GptRequest(model, data, 150);
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody() != null && responseEntity.getBody().getChoices() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                GptResponse.Choice choice = responseEntity.getBody().getChoices().get(0);
                if (choice != null && choice.getMessage() != null) {
                    String resp = choice.getMessage().getContent();
                    Flashcard card = parseFlashcardFromResponse(resp, subject);
                    if (card != null) {
                    	flashCardRepository.save(card);
                        flashcards.add(card);
                    }
                }
            }
        }
        return flashcards;
    }
    
    private Flashcard parseFlashcardFromResponse(String response, String subject) {
        Flashcard flashcard = new Flashcard();
        String[] lines = response.split("\n");

        for (String line : lines) {
			if (line.startsWith("Q:")) {
				flashcard.setQuestion(line.replace("Q: ", "").trim());
			} else if (line.startsWith("A:")) {
				flashcard.setAnswer(line.replace("A: ", "").trim());
			}
		}


        // Set the subject, question, and answer of the flashcard
        flashcard.setSubject(subject);


        // Ensure that both question and answer are not empty
        if (flashcard.getQuestion().isEmpty() || flashcard.getAnswer().isEmpty()) {
            // Handle the case where the response does not properly format a flashcard
            return null; // Or handle this case as appropriate for your application
        }

        return flashcard;
    }
    
}
