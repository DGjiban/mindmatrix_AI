// ============================
//  Startup Animation
// ============================

function initStartupAnimation() {
    const startup = document.getElementById('startup');
    const mainContent = document.getElementById('main-content');

    if (!startup || !mainContent) {
        console.error('Startup or main content element missing.');
        return;
    }

    mainContent.style.opacity = 0;
    mainContent.style.visibility = 'hidden';

    function onScroll() {
        startup.style.opacity = 0;
        startup.style.visibility = 'hidden';
        setTimeout(() => {
            startup.style.position = 'absolute';
        }, 500);
        mainContent.style.opacity = 1;
        mainContent.style.visibility = 'visible';
        window.removeEventListener('scroll', onScroll);
    }

    window.addEventListener('scroll', onScroll);
}

document.addEventListener('DOMContentLoaded', initStartupAnimation);

// ============================
//  Login / Sign-Up Logic
// ============================

// Show login form and hide sign-up form
function showLoginForm() {
    document.getElementById("login-section").style.display = "block";
    document.getElementById("signup-section").style.display = "none";
    document.getElementById("login-btn").classList.add("active");
    document.getElementById("signup-btn").classList.remove("active");
}

// Show sign-up form and hide login form
function showSignUpForm() {
    document.getElementById("signup-section").style.display = "block";
    document.getElementById("login-section").style.display = "none";
    document.getElementById("signup-btn").classList.add("active");
    document.getElementById("login-btn").classList.remove("active");
}

// Handle user login
async function loginUser() {
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ email: email, password: password })
        });

        const result = await response.json();
        console.log('Login response:', result);

        if (response.ok) {
            localStorage.setItem('authToken', 'someAuthToken');  // Replace with real token
            localStorage.setItem('userName', result.name);       // Store the user's name
            console.log('User name stored in localStorage:', result.name);
            alert('Login successful! Welcome, ' + result.name);
            window.location.href = '/';  // Redirect to homepage
        } else {
            alert('Login failed: ' + result.message);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred during login.');
    }
}

// Handle user sign-up
async function signUpUser() {
    const name = document.getElementById("signup-name").value;
    const email = document.getElementById("signup-email").value;
    const password = document.getElementById("signup-password").value;

    try {
        const response = await fetch('/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                name: name,
                email: email,
                password: password
            })
        });

        const result = await response.text();
        if (response.ok) {
            alert('Sign-up successful! User ID: ' + result);
        } else {
            alert('Sign-up failed: ' + result);
        }
    } catch (error) {
        console.error('Error during sign-up:', error);
        alert('An error occurred during sign-up.');
    }
}

// Check if user is logged in and display their name and update the Login tab
function displayUserNameAndLoginStatus() {
    const userName = localStorage.getItem('userName');
    const authToken = localStorage.getItem('authToken');
    const loginTab = document.getElementById('login-tab');  // Login/Logout tab element

    if (authToken && userName) {
        // User is logged in, display the name and change Login to Logout
        document.getElementById('user-name').style.display = 'inline';
        document.getElementById('nickname').textContent = userName;

        // Change Login tab to Logout
        loginTab.textContent = 'Logout';
        loginTab.addEventListener('click', function(event) {
            event.preventDefault();
            logoutUser();
        });
    } else {
        // User is not logged in, ensure Login tab is displayed as "Login"
        loginTab.textContent = 'Login';
        loginTab.href = '/login';  // Make sure the tab points to the login page
    }
}

// Function to log out the user
function logoutUser() {
    // Clear localStorage (authToken and userName)
    localStorage.removeItem('authToken');
    localStorage.removeItem('userName');

    // Redirect the user to the login page after logging out
    alert('You have been logged out.');
    window.location.href = '/login';
}

// Call this function when the page loads
window.onload = function() {
    displayUserNameAndLoginStatus();  // Display user's name and login/logout status
};


// ============================
//  Flashcards, Quiz & Challenge Logic
// ============================

// Check if the user is logged in before accessing the challenge
function checkLoginBeforeChallenge() {
    const token = localStorage.getItem('authToken');
    console.log('Checking login status. Token:', token);

    if (!token) {
        alert('You need to log in to access the Challenge page.');
        window.location.href = '/login';
    } else {
        window.location.href = '/challenge';
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const challengeLink = document.getElementById('challenge-link');
    if (challengeLink) {
        challengeLink.addEventListener('click', function (event) {
            event.preventDefault();
            checkLoginBeforeChallenge();
        });
    }
});

// Flashcards Logic
function initFlashcards() {
    const flashcards = document.querySelectorAll('.flashcard-container');
    if (flashcards.length === 0) return;

    let currentIndex = 0;
    const totalFlashcards = flashcards.length;
    const counterElement = document.getElementById('flashcard-counter');

    function updateFlashcardCounter() {
        if (counterElement) counterElement.textContent = `${currentIndex + 1} of ${totalFlashcards}`;
    }

    function showFlashcard(index) {
        if (index < 0 || index >= totalFlashcards) return;

        flashcards.forEach((flashcard) => flashcard.style.display = 'none');
        flashcards[index].style.display = 'block';

        updateFlashcardCounter();
    }

    flashcards.forEach(container => {
        container.addEventListener('click', function () {
            const flashcard = this.querySelector('.flashcard');
            if (flashcard) flashcard.classList.toggle('is-flipped');
        });
    });

    document.getElementById('nextButton')?.addEventListener('click', function () {
        currentIndex = (currentIndex + 1) % totalFlashcards;
        showFlashcard(currentIndex);
    });

    document.getElementById('prevButton')?.addEventListener('click', function () {
        currentIndex = (currentIndex - 1 + totalFlashcards) % totalFlashcards;
        showFlashcard(currentIndex);
    });

    showFlashcard(currentIndex);
}

document.addEventListener('DOMContentLoaded', initFlashcards);

// Quiz Logic
function initQuiz() {
    const quizContainers = document.querySelectorAll('.quiz-container');
    if (quizContainers.length === 0) return;

    let currentQuizIndex = 0;
    const totalQuizzes = quizContainers.length;
    const counterElement = document.getElementById('quiz-counter');

    function updateQuizCounter() {
        if (counterElement) counterElement.textContent = `${currentQuizIndex + 1} of ${totalQuizzes}`;
    }

    function showQuiz(index) {
        if (index < 0 || index >= totalQuizzes) return;

        quizContainers.forEach((quiz) => quiz.style.display = 'none');
        quizContainers[index].style.display = 'block';

        updateQuizCounter();
    }

    document.getElementById('QuiznextButton')?.addEventListener('click', function () {
        if (currentQuizIndex < totalQuizzes - 1) {
            currentQuizIndex++;
            showQuiz(currentQuizIndex);
        }
    });

    document.getElementById('QuizprevButton')?.addEventListener('click', function () {
        if (currentQuizIndex > 0) {
            currentQuizIndex--;
            showQuiz(currentQuizIndex);
        }
    });

    document.getElementById('QuizFinishButton')?.addEventListener('click', function () {
        const answers = collectQuizAnswers();
        displayQuizResults(answers);
        displayShareLinks();
    });

    function collectQuizAnswers() {
        const answers = [];
        quizContainers.forEach((quiz) => {
            const quizId = quiz.getAttribute('data-quiz-id');
            const selectedInput = quiz.querySelector('input[type="radio"]:checked');

            if (selectedInput) {
                const labelText = quiz.querySelector(`label[for="${selectedInput.id}"]`).innerText;
                answers.push({ quizId, selectedAnswer: labelText });
            }
        });
        return answers;
    }

    function displayQuizResults(answers) {
        fetch('/quizzes/verify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(answers)
        })
            .then(response => response.json())
            .then(data => {
                document.getElementById('quiz-results').innerHTML = `You answered correctly ${data.correctCount} out of ${data.total} questions.`;
                document.getElementById('quiz-results').style.display = 'block';
            })
            .catch(error => console.error('Error fetching quiz results:', error));
    }

    function displayShareLinks() {
        document.getElementById('shareLinks').style.display = 'block';
    }

    showQuiz(currentQuizIndex);
}

document.addEventListener('DOMContentLoaded', function () {
    initQuiz();
    initQuizTimer();
    initShowAnswers();
});

// Function to extract query parameters from URL
function getQueryParams(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Timer functionality using the user-selected time
function initQuizTimer() {
    const timerElement = document.getElementById('timer');

    // Only proceed if the timer element exists (i.e., we're on the quiz page)
    if (!timerElement) {
        console.log("No timer element found on this page. Skipping timer initialization.");
        return;  // Stop execution if the timer element is missing
    }

    // Get the selected time from query parameters (in minutes) and convert to seconds
    const quizTimerParam = getQueryParams('quizTimer');
    console.log('quizTimer from URL:', quizTimerParam); // Debugging timer value

    let timeLeft = parseInt(quizTimerParam) * 60;

    if (isNaN(timeLeft) || timeLeft <= 0) {
        console.error('Invalid timer value, setting to default 10 minutes.');
        timeLeft = 600; // Default to 10 minutes if invalid or missing
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
            autoSubmitQuiz(); // Automatically submit the quiz
        }
    }

    // Automatically click the "Finish" button when time is up
    function autoSubmitQuiz() {
        const finishButton = document.getElementById('QuizFinishButton');
        if (finishButton) {
            finishButton.click(); // Simulate a click on the "Finish" button
        } else {
            console.error("Finish button not found!");
        }
    }

    // Start the timer
    const timerInterval = setInterval(updateTimer, 1000);
    updateTimer();
}



// Show Answer functionality
function initShowAnswers() {
    const showAnswerButtons = document.querySelectorAll('.show-answer-button');

    showAnswerButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Find the closest quiz container
            const quizContainer = this.closest('.quiz-container');
            const correctAnswer = quizContainer?.querySelector('.correct-answer');
            if (correctAnswer) {
                correctAnswer.style.display = 'block'; // Show the correct answer
            } else {
                console.error('Correct answer element not found.');
            }
        });
    });
}
