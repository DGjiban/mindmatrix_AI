package ca.sheridancollege.mindmatrix.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.sheridancollege.mindmatrix.beans.Quiz;
import ca.sheridancollege.mindmatrix.repositories.QuizRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private GptService gptService;

    public List<Quiz> getOrCreateQuizzes(String subject, int number) {
        List<Quiz> quizzes = quizRepository.findBySubject(subject);

        // If no quizzes are found for the subject, generate new ones.
        if (quizzes.isEmpty()) {
            quizzes = gptService.generateQuizzes(subject, number);
            // Save each generated quiz to the database.
            quizzes.forEach(quizRepository::save);
        } else if (quizzes.size() > number) {
            // If more quizzes exist than requested, trim the list.
            quizzes = quizzes.subList(0, number);
        }

        return quizzes;
    }
}
