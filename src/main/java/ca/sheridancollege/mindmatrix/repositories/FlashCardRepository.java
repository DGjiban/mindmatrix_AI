package ca.sheridancollege.mindmatrix.repositories;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.mindmatrix.beans.Flashcard;

public interface FlashCardRepository extends CrudRepository<Flashcard, Long> {
	
	Flashcard findByQuestion(String question);
	
	Flashcard findByAnswer(String answer);
}
