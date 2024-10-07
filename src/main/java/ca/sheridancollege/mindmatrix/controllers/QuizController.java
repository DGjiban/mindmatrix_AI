package ca.sheridancollege.mindmatrix.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.mindmatrix.services.QuizService;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Test endpoint to retrieve all quiz document IDs (questions)
    @GetMapping("/documentIds")
    public List<String> getAllQuizDocumentIds() {
        try {
            // Retrieve and return all quiz document IDs
            return quizService.getAllQuizDocumentIds();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
