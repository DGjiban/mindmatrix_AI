package ca.sheridancollege.mindmatrix.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.sql.exec.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

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
    
    @Autowired
    private Firestore db;

    public void saveQuiz(Quiz quiz) {
   
        quizRepository.save(quiz);

        saveQuizToFirestore(quiz);
    }
    
    public void saveQuizToFirestore(Quiz quiz) {
        try {
            Map<String, Object> quizData = new HashMap<>();
            quizData.put("subject", quiz.getSubject());
            quizData.put("question", quiz.getQuestion());
            quizData.put("answers", quiz.getAnswers());
            quizData.put("correctAnswerText", quiz.getCorrectAnswerText());

            DocumentReference docRef = db.collection("quizzes").document(quiz.getQuestion());
            WriteResult result = docRef.set(quizData).get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving quiz to Firestore: " + e.getMessage());
        } catch (java.util.concurrent.ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    public List<Quiz> getOrCreateQuizzes(String subject, int number) {
        List<Quiz> quizzes = new ArrayList<Quiz>();
                
        // If no quizzes are found for the subject, generate new ones.
        if (quizzes.isEmpty()) {
            quizzes = generateQuizzes(subject, number);
            // Save each generated quiz to the database.
            quizzes.forEach(this::saveQuiz);
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
       
    	Quiz quiz = new Quiz();
        String[] lines = response.split("\n");
        String question = null;
        List<String> answers = new ArrayList<>();
        String correctAnswer = null;

        for (String line : lines) {
            

            if (line.startsWith("Q: ") || line.startsWith("Question: ")) {
            	
                question = line.replace("Q: ", "").replace("Question: ", "").trim();
                
            } else if (line.matches("^[A-D]\\) .*")) {
            	
                answers.add(line.substring(line.indexOf(") ") - 1).trim());
                
            } else if (line.startsWith("Correct ") || line.startsWith("Answer:")) {
                
            	correctAnswer = line.trim();
            }
        }
 

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
    
    public Quiz findQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    public List<Quiz> getAllQuizzes() throws InterruptedException, ExecutionException, java.util.concurrent.ExecutionException {
        List<Quiz> quizzes = new ArrayList<>();
        
        var quizDocs = db.collection("quizzes").get().get().getDocuments();

        for (var doc : quizDocs) {
            Quiz quiz = new Quiz();
            quiz.setSubject(doc.getString("subject"));
            quiz.setQuestion(doc.getString("question"));
            quiz.setAnswers((List<String>) doc.get("answers"));
            quiz.setCorrectAnswerText(doc.getString("correctAnswerText"));
            quizzes.add(quiz);
        }

        return quizzes;
    }
}
