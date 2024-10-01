package ca.sheridancollege.mindmatrix.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ca.sheridancollege.mindmatrix.beans.Flashcard;

@Repository
public interface FlashCardRepository extends JpaRepository<Flashcard, Long> {
    
    // Change to return a list, as multiple flashcards can have the same question
    List<Flashcard> findByQuestion(String question);

    // Change to return a list, as multiple flashcards can have the same answer
    List<Flashcard> findByAnswer(String answer);
}
