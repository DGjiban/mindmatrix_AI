package ca.sheridancollege.mindmatrix.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flashcard {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;
	
	@NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
	
}
