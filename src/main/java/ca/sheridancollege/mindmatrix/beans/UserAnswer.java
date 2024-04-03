package ca.sheridancollege.mindmatrix.beans;

public class UserAnswer {

	 private Long quizId;
	 private String selectedAnswer;
	
	 public UserAnswer(Long quizId, String selectedAnswer) {
		super();
		this.quizId = quizId;
		this.selectedAnswer = selectedAnswer;
	}

	public Long getQuizId() {
		return quizId;
	}

	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}

	public String getSelectedAnswer() {
		return selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}
	 
}
