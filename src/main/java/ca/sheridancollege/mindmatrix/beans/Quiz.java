package ca.sheridancollege.mindmatrix.beans;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;
	
	@NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

	@ElementCollection
	@CollectionTable(name = "quiz_answers", joinColumns = @JoinColumn(name = "quiz_id"))
	@Column(name = "answer")
	private List<String> answers;

	@NonNull
    private String correctAnswerIndex;
}
