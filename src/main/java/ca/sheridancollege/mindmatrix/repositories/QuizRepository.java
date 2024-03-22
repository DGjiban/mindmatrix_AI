package ca.sheridancollege.mindmatrix.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.sheridancollege.mindmatrix.beans.Quiz;

public interface QuizRepository extends CrudRepository<Quiz, Long> {
	
	Quiz findByQuestion(String question);
	
	List<Quiz> findBySubject(String subject);
}