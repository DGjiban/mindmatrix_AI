package ca.sheridancollege.mindmatrix.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.repositories.FlashCardRepository;

@Service
public class FlashcardService {
	@Autowired
    private FlashCardRepository flashCardRepository;

    @Autowired
    private GptService gptService; // Assume this is a service that wraps interactions with GptController

    public List<Flashcard> getOrCreateFlashcards(String subject, int number) {
        List<Flashcard> flashcards = flashCardRepository.findBySubject(subject);

        if (flashcards.isEmpty()) {
            flashcards = gptService.generateFlashcards(subject, number);
            // Assuming generateFlashcards now saves the flashcards to the database
            // and returns the list of generated flashcards
        } else if (flashcards.size() > number) {
            flashcards = flashcards.subList(0, number);
        }

        return flashcards;
    }
}
