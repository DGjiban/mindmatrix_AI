package ca.sheridancollege.mindmatrix.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.gpt.GptRequest;
import ca.sheridancollege.mindmatrix.gpt.GptResponse;
import ca.sheridancollege.mindmatrix.repositories.QuizRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    
	@Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;
    
    public List<Quiz> getOrCreateQuizzes(String subject, int number) {
        List<Quiz> quizzes = quizRepository.findBySubject(subject);

        // If no quizzes are found for the subject, generate new ones.
        if (quizzes.isEmpty()) {
            quizzes = generateQuizzes(subject, number);
            // Save each generated quiz to the database.
            quizzes.forEach(quizRepository::save);
        } else if (quizzes.size() > number) {
            // If more quizzes exist than requested, trim the list.
            quizzes = quizzes.subList(0, number);
        }

        return quizzes;
    }
    
 // Method to generate quizzes with multiple-choice questions
    public List<Quiz> generateQuizzes(String subject, int number) {
        List<Quiz> quizzes = new ArrayList<>();
        for (int i = 0; i < number; i++) {
        	
        	String prompt = "Create a multiple choice question about " + subject + ", Identify the question by stating with 'Question: ' "
        			+ " Identify the answers with A), B), C), D) and Indicate the correct answer by stating 'Correct answer: [option letter].' at the end.";

            GptRequest request = new GptRequest(model, prompt, 250); // Adjust max tokens if necessary
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            if (responseEntity.getBody() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                GptResponse.Choice choice = responseEntity.getBody().getChoices().get(0);
                if (choice != null && choice.getMessage() != null) {
                    String response = choice.getMessage().getContent();
                    
                    System.out.println(response);
                    
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
       
    	//System.out.println("Full response from AI: " + response);
    	Quiz quiz = new Quiz();
        String[] lines = response.split("\n");
        String question = null;
        List<String> answers = new ArrayList<>();
        String correctAnswer = null;

        for (String line : lines) {
            
        	//System.out.println("Processing line: " + line);

            if (line.startsWith("Q: ") || line.startsWith("Question: ")) {
            	
                question = line.replace("Q: ", "").replace("Question: ", "").trim();
                
            } else if (line.matches("^[A-D]\\) .*")) {
            	
                answers.add(line.substring(line.indexOf(") ") - 1).trim());
                
            } else if (line.startsWith("Correct Answer:") || line.startsWith("Answer:")) {
                
            	correctAnswer = line.substring(line.indexOf(": ") + 2).trim();
            }
        }
 
        System.out.println("*Question: " + question);
        System.out.println("*Answers: " + answers);
        System.out.println("*Correct Answer is: " + correctAnswer);

        if (question != null && !answers.isEmpty() && correctAnswer != null) {
            quiz.setSubject(subject);
        	quiz.setQuestion(question);
        	quiz.setAnswers(answers);
        	quiz.setCorrectAnswerText(correctAnswer);
        	
        	return quiz;
        } else {
            System.out.println("Failed to organize question and answers properly.");
            return null;
        }
    }
    	
    	/*String[] parts = response.split("\n");
        Quiz quiz = new Quiz();
        quiz.setSubject(subject);
        List<String> answers = new ArrayList<>();
        String correctAnswerText = null;

        //quiz.setQuestion(lines.length > 0 ? lines[0].trim() : "");

        // Skip the first part assuming it's the question repeated as an answer.
        for (int i = 1; i < parts.length; i++) {
            String trimmedPart = parts[i].trim();
            if (!trimmedPart.isEmpty()) {
                // Check if the part indicates a correct answer or is a regular answer
                if (trimmedPart.matches("^\\*[A-D]\\) .+") 
                		|| trimmedPart.startsWith("The correct answer is:")
                		|| trimmedPart.startsWith("*The correct answer is: ") 
                		|| trimmedPart.startsWith("Question:") 
                		|| trimmedPart.startsWith("Question")) {
                	
                    correctAnswerText = trimmedPart.replaceAll("The correct answer is:  ", "").trim();
                    
                    // If it's marked as the correct answer explicitly, don't add to answers list
                    if (!trimmedPart.startsWith("The correct answer is  ")) {
                        answers.add(trimmedPart.replaceFirst("^\\*", "").trim()); // Remove the "*" before adding
                    }
                } else {
                    answers.add(trimmedPart);
                }
            }
        }
        
        System.out.println(correctAnswerText);
        
        quiz.setAnswers(answers);
        quiz.setCorrectAnswerText(correctAnswerText);
        return quiz;
    }*/

}
