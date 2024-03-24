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
public class GptService {
	@Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private FlashCardRepository flashcardRepository;

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
                        flashcardRepository.save(card);
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
        StringBuilder question = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        boolean isAnswer = false;

        for (String line : lines) {
            if (line.startsWith("Q:")) {
                // Start of the question part
                isAnswer = false;
                // Append this line to the question StringBuilder, removing the "Q: " part
                question.append(line.substring(2).trim()).append(" ");
            } else if (line.startsWith("A:")) {
                // Start of the answer part
                isAnswer = true;
                // Append this line to the answer StringBuilder, removing the "A: " part
                answer.append(line.substring(2).trim()).append(" ");
            } else {
                // Continuation of the current part (question or answer)
                if (isAnswer) {
                    answer.append(line.trim()).append(" ");
                } else {
                    question.append(line.trim()).append(" ");
                }
            }
        }

        // Set the subject, question, and answer of the flashcard
        flashcard.setSubject(subject);
        flashcard.setQuestion(question.toString().trim());
        flashcard.setAnswer(answer.toString().trim());

        // Ensure that both question and answer are not empty
        if (flashcard.getQuestion().isEmpty() || flashcard.getAnswer().isEmpty()) {
            // Handle the case where the response does not properly format a flashcard
            return null; // Or handle this case as appropriate for your application
        }

        return flashcard;
    }
    
    // Additional methods as needed for quizzes, etc.
}
