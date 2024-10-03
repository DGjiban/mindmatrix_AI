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
        // Initialize a list to store generated quizzes
        List<Quiz> quizzes = new ArrayList<>();

        // Loop to generate the specified number of quizzes
        for (int i = 0; i < number; i++) {
            
            // Prompt to instruct GPT to create a multiple-choice question based on the given subject
            String prompt = "Create a multiple choice question about " + subject + 
                            ", Identify the question by stating with 'Question: ' " +
                            "Identify the answers with A), B), C), D) and Indicate the correct answer " +
                            "by stating 'Correct answer: [option letter].' at the end.";

            // Create a GPT request object with the given model, prompt, and token limit (max tokens)
            GptRequest request = new GptRequest(model, prompt, 250); // Adjust max tokens if necessary

            // Send the request to the GPT API and receive the response
            ResponseEntity<GptResponse> responseEntity = template.postForEntity(apiURL, request, GptResponse.class);

            // Check if the response body contains valid data and if it has choices
            if (responseEntity.getBody() != null && !responseEntity.getBody().getChoices().isEmpty()) {
                
                // Get the first choice from the response
                GptResponse.Choice choice = responseEntity.getBody().getChoices().get(0);
                
                // Check if the choice and its message content are valid
                if (choice != null && choice.getMessage() != null) {
                    String response = choice.getMessage().getContent();  // Extract the content from the response
                    
                    // Print the generated question for debugging
                    System.out.println(response);
                    
                    // Parse the response content to create a Quiz object
                    Quiz quiz = parseQuizFromResponse(response, subject);
                    
                    // If the quiz object is valid, save it to the quiz repository and add to the quiz list
                    if (quiz != null) {
                        quizRepository.save(quiz);  // Save the quiz to the repository
                        quizzes.add(quiz);          // Add the quiz to the list of quizzes
                    }
                }
            }
        }
        // Return the list of generated quizzes
        return quizzes;
    }

    
 // Method to parse the response from the AI and extract question, answers, and correct answer
    private Quiz parseQuizFromResponse(String response, String subject) {
        
        // Initialize a new Quiz object
        Quiz quiz = new Quiz();

        // Split the response into lines for easier processing
        String[] lines = response.split("\n");

        // Declare variables for the question, a list of answers, and the correct answer
        String question = null;
        List<String> answers = new ArrayList<>();
        String correctAnswer = null;

        // Iterate through each line in the response to extract information
        for (String line : lines) {
            
            // Check if the line contains the question, which starts with "Q: " or "Question: "
            if (line.startsWith("Q: ") || line.startsWith("Question: ")) {
                
                // Remove the prefix and trim any extra spaces to get the question text
                question = line.replace("Q: ", "").replace("Question: ", "").trim();
                
            // Check if the line contains one of the answers, identified by "A)" to "D)"
            } else if (line.matches("^[A-D]\\) .*")) {
                
                // Extract the answer text and add it to the list of answers
                answers.add(line.substring(line.indexOf(") ") - 1).trim());
                
            // Check if the line contains the correct answer, identified by "Correct" or "Answer:"
            } else if (line.startsWith("Correct ") || line.startsWith("Answer:")) {
                
                // Trim the line to capture the correct answer
                correctAnswer = line.trim();
            }
        }

        // Ensure that the question, answers, and correct answer are all non-null and valid
        if (question != null && !answers.isEmpty() && correctAnswer != null) {
            
            // Set the subject, question, answers, and correct answer in the Quiz object
            quiz.setSubject(subject);
            quiz.setQuestion(question);
            quiz.setAnswers(answers);
            quiz.setCorrectAnswerText(correctAnswer);
            
            // Return the populated Quiz object
            return quiz;
        } else {
            // Print an error message if parsing failed and return null
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
