package ca.sheridancollege.mindmatrix.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.gpt.GptRequest;
import ca.sheridancollege.mindmatrix.gpt.GptResponse;
import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;
import ca.sheridancollege.mindmatrix.repositories.QuizRepository;

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
    
    @Autowired
    private QuizRepository quizRepository;

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
                    System.out.println(resp);
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
    
 // Method to generate quizzes with multiple-choice questions
    public List<Quiz> generateQuizzes(String subject, int number) {
        List<Quiz> quizzes = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            String prompt = "Generate a multiple-choice question about " + subject + ". List four options and mark the correct answer with a '*'.";
            GptRequest request = new GptRequest(model, prompt, 250); // Adjust max tokens if necessary
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                GptResponse.Choice choice = responseEntity.getBody().getChoices().get(0);
                if (choice != null && choice.getMessage() != null) {
                    String response = choice.getMessage().getContent();
                    Quiz quiz = parseQuizFromResponse(response, subject);
                    if (quiz != null) {
                        quizRepository.save(quiz);
                        quizzes.add(quiz);
                    }
                }
            }
        }
        return quizzes;
    }
    
    private Quiz parseQuizFromResponse(String response, String subject) {
        String[] parts = response.split("\n");
        Quiz quiz = new Quiz();
        quiz.setSubject(subject);
        List<String> answers = new ArrayList<>();
        String correctAnswerText = null;

        quiz.setQuestion(parts.length > 0 ? parts[0].trim() : "");

        // Skip the first part assuming it's the question repeated as an answer.
        for (int i = 1; i < parts.length; i++) {
            String trimmedPart = parts[i].trim();
            if (!trimmedPart.isEmpty()) {
                // Check if the part indicates a correct answer or is a regular answer
                if (trimmedPart.matches("^\\*[A-D]\\) .+") || trimmedPart.startsWith("Correct answer:") || trimmedPart.startsWith("*Correct answer:")) {
                    correctAnswerText = trimmedPart.replaceAll("^\\*|Correct answer:", "").trim();
                    // If it's marked as the correct answer explicitly, don't add to answers list
                    if (!trimmedPart.startsWith("Correct answer:")) {
                        answers.add(trimmedPart.replaceFirst("^\\*", "").trim()); // Remove the "*" before adding
                    }
                } else {
                    answers.add(trimmedPart);
                }
            }
        }

        // Assuming the correct answer is already in the answers, we find its index.
        if (correctAnswerText != null) {
            for (int i = 0; i < answers.size(); i++) {
                if (answers.get(i).contains(correctAnswerText)) {
                    quiz.setCorrectAnswerIndex(String.valueOf(i));
                    break;
                }
            }
        }

        quiz.setAnswers(answers);
        return quiz;
    }





    

}
