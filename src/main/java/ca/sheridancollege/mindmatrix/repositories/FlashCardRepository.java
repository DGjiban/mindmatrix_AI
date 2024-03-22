package ca.sheridancollege.mindmatrix.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.mindmatrix.beans.Flashcard;
import ca.sheridancollege.mindmatrix.beans.Quiz;

public interface FlashCardRepository extends CrudRepository<Flashcard, Long> {
	
	Flashcard findByQuestion(String question);
	
	Flashcard findByAnswer(String answer);
	
	List<Flashcard> findBySubject(String subject);
}
