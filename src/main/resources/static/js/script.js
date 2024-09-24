window.onload = function() {
	const startup = document.getElementById('startup');
	const mainContent = document.getElementById('main-content');

	function onScroll() {
		
		startup.style.opacity = 0;
		startup.style.visibility = 'hidden';
		// Set startup to absolute position after the transition to remove it from the flow
		setTimeout(() => {
			startup.style.position = 'absolute';
		}, 500); // Match this delay with the transition duration

		// Fade in the main content
		mainContent.style.opacity = 1;
		mainContent.style.visibility = 'visible';

		// Remove the scroll event listener
		window.removeEventListener('scroll', onScroll);
	}

	window.addEventListener('scroll', onScroll);
};

// Flash JS
document.addEventListener("DOMContentLoaded", function() {
	const flashcards = document.querySelectorAll('.flashcard-container');
	let currentIndex = 0;
	const counterElement = document.getElementById('flashcard-counter');
	const totalFlashcards = flashcards.length;

	// Update the flashcard counter
	function updateFlashcardCounter() {
		counterElement.textContent = `${currentIndex + 1} of ${totalFlashcards}`;
	}

	// Show the first flashcard initially, hide all others
	function showFlashcard(index) {
		// Hide all flashcards
		flashcards.forEach((flashcard, i) => {
			flashcard.style.display = 'none';
		});
		// Show the current flashcard
		flashcards[index].style.display = 'block';
		// Update the flashcard counter
		updateFlashcardCounter();
	}

	document.querySelectorAll('.flashcard-container').forEach(container => {
		container.addEventListener('click', function() {
			const flashcard = this.querySelector('.flashcard');
			flashcard.classList.toggle('is-flipped');
		});
	});

	// Next button click event
	document.getElementById('nextButton').addEventListener('click', function() {
		currentIndex = (currentIndex + 1) % totalFlashcards; // Wrap around to the first flashcard
		showFlashcard(currentIndex);
	});

	// Previous button click event
	document.getElementById('prevButton').addEventListener('click', function() {
		currentIndex = (currentIndex - 1 + totalFlashcards) % totalFlashcards; // Wrap around to the last flashcard
		showFlashcard(currentIndex);
	});

	// Initialize the first flashcard and counter
	showFlashcard(currentIndex);
});

document.addEventListener("DOMContentLoaded", function() {
	const quizContainers = document.querySelectorAll('.quiz-container');
	let currentQuizIndex = 0;
	const totalQuizzes = quizContainers.length;
	const counterElement = document.getElementById('quiz-counter');

	// update counter
	function updateQuizCounter() {
		counterElement.textContent = `${currentQuizIndex + 1} of ${totalQuizzes}`;
	}

	// show the current quiz
	function showQuiz(index) {
		// hide the questions
		quizContainers.forEach((container, i) => {
			container.style.display = 'none';
		});
		// show the current question
		quizContainers[index].style.display = 'block';
		// call the update counter function
		updateQuizCounter();
	}

	// Prev button
	document.getElementById('QuizprevButton').addEventListener('click', function() {
		if (currentQuizIndex > 0) {
			currentQuizIndex--;
			showQuiz(currentQuizIndex);
		}
	});

	// Next button
	document.getElementById('QuiznextButton').addEventListener('click', function() {
		if (currentQuizIndex < totalQuizzes - 1) {
			currentQuizIndex++;
			showQuiz(currentQuizIndex);
		}
	});

	// start the quiz
	showQuiz(currentQuizIndex);
});


document.addEventListener("DOMContentLoaded", function() {
	document.getElementById('QuizFinishButton').addEventListener('click', function() {
		loadShareThisScript();
		
		document.getElementById('shareLinks').style.display = 'block';
		
		const answers = [];

		document.querySelectorAll('.quiz-container').forEach(container => {
			const quizId = container.getAttribute('data-quiz-id');
			const selectedInput = container.querySelector('input[type="radio"]:checked');

			if (selectedInput) {
				
				const labelText = container.querySelector(`label[for="${selectedInput.id}"]`).innerText;

				answers.push({
					quizId: quizId,
					selectedAnswer: labelText
				});
			}
		});

		fetch('/quizzes/verify', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(answers)
		})
			.then(response => response.json())
			.then(data => {
				document.getElementById('quiz-results').innerHTML = `You answered correctly ${data.correctAnswers} out of ${data.totalQuestions} questions.`;
				document.getElementById('quiz-results').style.display = 'block';
			})
			.catch(error => console.error('Error:', error));
	document.querySelectorAll('.show-answer-button').forEach(button => {
		button.addEventListener('click', function() {
			
			const quizContainer = this.closest('.quiz-container');
			
			const correctAnswer = quizContainer.querySelector('.correct-answer');
			correctAnswer.style.display = 'block';
		});
	});
	});
});


//Function to load ShareThis script
function loadShareThisScript(){
	const script = document.createElement('script');
		script.type = 'text/javascript';
		script.src = 'https://platform-api.sharethis.com/js/sharethis.js#property=6601a93bfb0d8000121105be&product=inline-share-buttons&source=platform';
		script.async = true;
		document.head.appendChild(script);
	}


//Timer
function storeInputValues() {

const quizTimerValue = document.getElementById('quizTimer').value;

localStorage.setItem('quizTimer', quizTimerValue);

//start timer
		let timer = quizTimerValue;
		
          const interval = setInterval(() => {
                       
               if (timer <= 0) {						
				clearInterval(interval);
					
				window.location.href = 'quiz.html';
 				 				
 			}	 localStorage.setItem('timer', timer);
 				 
             }, 1000);  
      }
   
     const quizTimerValue = localStorage.getItem('timer');
	 document.getElementById('quizTimer').textContent = quizTimerValue;
	 
const s = quizTimerValue;
let time = s * 60;
const timerEl = document.getElementById('timer');

const a = setInterval(updateTimer, 1000);

function updateTimer()
{
	const minutes = Math.floor(time / 60);
	let seconds = time % 60;
	timerEl.innerHTML = `${minutes}:${seconds}`;
	time--;
	
	if(time < 0)
	{

		clearTimeout(a);
		document.getElementById("timer").innerHTML = "Times Up!";
	}
}
document.addEventListener("DOMContentLoaded", function () {
    const cards = document.querySelectorAll('.card');
    let currentCardIndex = 0;
    const answers = [];
    const answerMapping = ['A', 'B', 'C', 'D']; // Mapping for answer options

    // Function to update the card stack positions
    const updateCardStack = () => {
        console.log(`Updating card stack. Current index: ${currentCardIndex}`);
        cards.forEach((card, index) => {
            const relativeIndex = (index - currentCardIndex + cards.length) % cards.length;
            card.style.zIndex = cards.length - relativeIndex;

            if (relativeIndex === 0) {
                card.style.transform = 'translateY(0px) scale(1)';
                card.style.opacity = 1;
            } else if (relativeIndex === 1) {
                card.style.transform = 'translateY(10px) scale(0.98)';
                card.style.opacity = 0.8;
            } else if (relativeIndex === 2) {
                card.style.transform = 'translateY(20px) scale(0.96)';
                card.style.opacity = 0.6;
            } else {
                card.style.opacity = 0; // Hide remaining cards
            }
        });
    };

    // Initially set the correct card positions
    updateCardStack();

    document.querySelectorAll('.next-button').forEach(button => {
        button.addEventListener('click', () => {
            console.log('Next button clicked'); // Debug line
            const currentCard = cards[currentCardIndex];
            const quizId = currentCard.getAttribute('data-quiz-id');
            const selectedAnswer = currentCard.querySelector('input[type="radio"]:checked');

            if (selectedAnswer) {
                answers.push({
                    quizId: quizId,
                    selectedAnswer: selectedAnswer.value
                });
            }

            currentCardIndex++;

            if (currentCardIndex < cards.length) {
                updateCardStack();
            } else {
                document.getElementById('show-answers-btn').style.display = 'block';
            }
        });
    });

    document.getElementById('show-answers-btn').addEventListener('click', function () {
        fetch('/quizzes/verify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(answers)
        })
        .then(response => response.json())
        .then(result => {
            const resultsBody = document.getElementById('results-body');
            resultsBody.innerHTML = ''; // Clear any previous results

            result.answers.forEach(answer => {
                const selectedAnswer = answerMapping[answer.selectedAnswer]; // Convert numeric value to alphabet
                const correctAnswer = answer.correctAnswer.replace('Correct answer: ', ''); // Clean up correct answer text

                const row = document.createElement('tr');
                row.innerHTML = `<td>${answer.question}</td><td>${selectedAnswer}</td><td>${correctAnswer}</td>`;
                resultsBody.appendChild(row);
            });

            // Show the table
            document.getElementById('results-table').style.display = 'table';
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
});
