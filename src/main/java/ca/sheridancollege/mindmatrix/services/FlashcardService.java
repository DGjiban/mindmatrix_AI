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

    public List<Flashcard> generateFlashcards(String subject, int number) {
        List<Flashcard> flashcards = new ArrayList<>();

        for (int i = number; flashcards.size() < number; i--) {
            String data = "always use the format Q: and A: and with short answer and question, " + subject;
            GptRequest request = new GptRequest(model, data, 1000);
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody() != null && responseEntity.getBody().getChoices() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                GptResponse.Choice choice = responseEntity.getBody().getChoices().get(0);
                if (choice != null && choice.getMessage() != null) {
                    String resp = choice.getMessage().getContent();
                    Flashcard card = parseFlashcardFromResponse(resp, subject);

                    if (card != null) {
                        // Check if a flashcard with the same question or answer exists
                       // boolean exists = !flashCardRepository.findByQuestion(card.getQuestion()).isEmpty()
                         //                || !flashCardRepository.findByAnswer(card.getAnswer()).isEmpty();

                        if (!exists) {
                            flashCardRepository.save(card);
                            flashcards.add(card);
                        } else {
                            i++; // Adjust the loop counter if a duplicate is found
                        }
                    }
                }
            }
        }

        return flashcards;
    }

    // Parse the GPT response into a Flashcard object
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

        flashcard.setSubject(subject);

        // Ensure that both question and answer are not empty
        if (flashcard.getQuestion().isEmpty() || flashcard.getAnswer().isEmpty()) {
            return null;
        }

        return flashcard;
    }
}
