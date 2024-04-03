package ca.sheridancollege.mindmatrix.beans;

public class QuizResult {
	private int correctAnswers;
    private int totalQuestions;

    public QuizResult(int correctAnswers, int totalQuestions) {
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
    }

    // Getters
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

}