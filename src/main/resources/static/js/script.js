// Function to initialize the startup animation
function initStartupAnimation() {
    const startup = document.getElementById('startup');
    const mainContent = document.getElementById('main-content');

    // Ensure both elements exist
    if (!startup || !mainContent) {
        console.error('Startup or main content element missing.');
        return;
    }

    // Ensure main content is initially hidden
    mainContent.style.opacity = 0;
    mainContent.style.visibility = 'hidden';

    function onScroll() {
        startup.style.opacity = 0;
        startup.style.visibility = 'hidden';
        
        setTimeout(() => {
            startup.style.position = 'absolute'; // hide startup after animation
        }, 500); // Adjust time to match your CSS transition if needed

        mainContent.style.opacity = 1;
        mainContent.style.visibility = 'visible';

        // Remove the scroll event listener once the animation is done
        window.removeEventListener('scroll', onScroll);
    }

    // Attach scroll listener
    window.addEventListener('scroll', onScroll);
}

// Initialize startup animation when the DOM is fully loaded
document.addEventListener('DOMContentLoaded', initStartupAnimation);


// Function to check if the user is logged in
function checkLoginBeforeChallenge() {
    const token = localStorage.getItem('authToken');

    // Debugging log to verify the token is being checked
    console.log('Checking login status. Token:', token);

    if (!token) {
        alert('You need to log in to access the Challenge page.');
        window.location.href = '/login'; // Redirect to login if no token
    } else {
        window.location.href = '/challenge'; // Redirect to challenge page if token exists
    }
}

// Add click event listener to Challenge tab only if it exists
document.addEventListener("DOMContentLoaded", function() {
    const challengeLink = document.getElementById('challenge-link');
    
    // Ensure the challenge link exists
    if (challengeLink) {
        challengeLink.addEventListener('click', function(event) {
            event.preventDefault(); // Prevent default anchor behavior
            checkLoginBeforeChallenge(); // Call login check function
        });
    } else {
        console.error('Challenge link not found.');
    }
});

// Function to initialize the flashcard functionality
function initFlashcards() {
    const flashcards = document.querySelectorAll('.flashcard-container');
    if (flashcards.length === 0) {
        console.log("No flashcards found.");
        return; // Exit if no flashcards exist on the page
    }

    let currentIndex = 0;
    const totalFlashcards = flashcards.length;
    const counterElement = document.getElementById('flashcard-counter');

    // Function to update the flashcard counter
    function updateFlashcardCounter() {
        if (counterElement) {
            counterElement.textContent = `${currentIndex + 1} of ${totalFlashcards}`;
        }
    }

    // Function to show a specific flashcard
    function showFlashcard(index) {
        // Ensure the index is within bounds
        if (index < 0 || index >= totalFlashcards) {
            console.error('Invalid flashcard index:', index);
            return;
        }

        // Hide all flashcards and show the current one
        flashcards.forEach((flashcard) => flashcard.style.display = 'none');
        flashcards[index].style.display = 'block';

        // Update the counter
        updateFlashcardCounter();
    }

    // Event listener for flipping the flashcard
    flashcards.forEach(container => {
        container.addEventListener('click', function() {
            const flashcard = this.querySelector('.flashcard');
            if (flashcard) {
                flashcard.classList.toggle('is-flipped');
            }
        });
    });

    // Event listener for the "Next" button
    const nextButton = document.getElementById('nextButton');
    if (nextButton) {
        nextButton.addEventListener('click', function() {
            currentIndex = (currentIndex + 1) % totalFlashcards; // Cycle through flashcards
            showFlashcard(currentIndex);
        });
    }

    // Event listener for the "Previous" button
    const prevButton = document.getElementById('prevButton');
    if (prevButton) {
        prevButton.addEventListener('click', function() {
            currentIndex = (currentIndex - 1 + totalFlashcards) % totalFlashcards; // Cycle backwards
            showFlashcard(currentIndex);
        });
    }

    // Initialize the first flashcard
    showFlashcard(currentIndex);
}

// Initialize flashcard functionality when DOM is ready
document.addEventListener('DOMContentLoaded', initFlashcards);


// Function to initialize the quiz functionality
function initQuiz() {
    const quizContainers = document.querySelectorAll('.quiz-container');
    if (quizContainers.length === 0) {
        console.log("No quizzes found.");
        return; // Exit if no quizzes exist on the page
    }

    let currentQuizIndex = 0;
    const totalQuizzes = quizContainers.length;
    const counterElement = document.getElementById('quiz-counter');

    // Function to update the quiz counter
    function updateQuizCounter() {
        if (counterElement) {
            counterElement.textContent = `${currentQuizIndex + 1} of ${totalQuizzes}`;
        }
    }

    // Function to show a specific quiz question
    function showQuiz(index) {
        if (index < 0 || index >= totalQuizzes) {
            console.error('Invalid quiz index:', index);
            return;
        }

        // Hide all quizzes and show the current one
        quizContainers.forEach((quiz) => quiz.style.display = 'none');
        quizContainers[index].style.display = 'block';

        // Update the quiz counter
        updateQuizCounter();
    }

    // Event listener for the "Next" button
    const nextQuizButton = document.getElementById('QuiznextButton');
    if (nextQuizButton) {
        nextQuizButton.addEventListener('click', function() {
            if (currentQuizIndex < totalQuizzes - 1) {
                currentQuizIndex++;
                showQuiz(currentQuizIndex);
            }
        });
    }

    // Event listener for the "Previous" button
    const prevQuizButton = document.getElementById('QuizprevButton');
    if (prevQuizButton) {
        prevQuizButton.addEventListener('click', function() {
            if (currentQuizIndex > 0) {
                currentQuizIndex--;
                showQuiz(currentQuizIndex);
            }
        });
    }

    // Event listener for the "Finish" button
    const finishQuizButton = document.getElementById('QuizFinishButton');
    if (finishQuizButton) {
        finishQuizButton.addEventListener('click', function() {
            // Collect the answers when the quiz is finished
            const answers = collectQuizAnswers();
            displayQuizResults(answers);
            displayShareLinks(); // Show share buttons
        });
    }

    // Function to collect the selected answers
    function collectQuizAnswers() {
        const answers = [];

        quizContainers.forEach((quiz) => {
            const quizId = quiz.getAttribute('data-quiz-id');
            const selectedInput = quiz.querySelector('input[type="radio"]:checked');

            if (selectedInput) {
                const labelText = quiz.querySelector(`label[for="${selectedInput.id}"]`).innerText;
                answers.push({
                    quizId: quizId,
                    selectedAnswer: labelText
                });
            }
        });

        return answers;
    }

    // Function to display the quiz results
    function displayQuizResults(answers) {
        fetch('/quizzes/verify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(answers)
        })
        .then(response => response.json())
        .then(data => {
            // Display the results based on the server response
            const resultContainer = document.getElementById('quiz-results');
            resultContainer.innerHTML = `You answered correctly ${data.correctCount} out of ${data.total} questions.`;
            resultContainer.style.display = 'block';
        })
        .catch(error => {
            console.error('Error fetching quiz results:', error);
        });
    }
    
    

    // Function to show the share links after finishing the quiz
    function displayShareLinks() {
        const shareLinks = document.getElementById('shareLinks');
        if (shareLinks) {
            shareLinks.style.display = 'block';
        }
    }

    // Initialize the first quiz question
    showQuiz(currentQuizIndex);
}

// Function to extract query parameters from URL
function getQueryParams(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Timer functionality using the user-selected time
function initQuizTimer() {
    const timerElement = document.getElementById('timer');
    const quizTimerElement = document.getElementById('quizTimer');
    
    // Get the selected time from query parameters (in minutes)
    let timeLeft = parseInt(getQueryParams('quizTimer')) * 60; // Convert to seconds

    if (isNaN(timeLeft) || timeLeft <= 0) {
        //console.error('Invalid timer value, setting to default 10 minutes.');
        timeLeft = 600; // Default to 10 minutes if invalid or missing
    }

    if (!timerElement || !quizTimerElement) {
        console.log("Timer elements not found.");
        return;
    }

    // Function to update the timer display
    function updateTimer() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timerElement.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

        if (timeLeft > 0) {
            timeLeft--;
        } else {
            clearInterval(timerInterval); // Stop the timer when time runs out
            document.getElementById("quiz-results").innerHTML = "Time's Up!";
            // Optionally, you can auto-submit the quiz here
        }
    }

    // Start the timer
    const timerInterval = setInterval(updateTimer, 1000);
    updateTimer(); // Run the timer immediately
}

// Initialize quiz and other functionalities when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    initQuiz();        // Initialize quiz navigation and submission
    initQuizTimer();   // Initialize the quiz timer
    initShowAnswers(); // Initialize "Show Answer" button functionality
});

// Function to initialize the show answer functionality
function initShowAnswers() {
    const showAnswerButtons = document.querySelectorAll('.show-answer-button');
    
    showAnswerButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Find the closest quiz container
            const quizContainer = this.closest('.quiz-container');
            if (quizContainer) {
                // Find the correct answer element inside that quiz container
                const correctAnswer = quizContainer.querySelector('.correct-answer');
                if (correctAnswer) {
                    correctAnswer.style.display = 'block'; // Show the correct answer
                }
            }
        });
    });
}


