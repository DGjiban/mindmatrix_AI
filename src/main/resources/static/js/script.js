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

// index tab function
document.addEventListener('DOMContentLoaded', function () {
    // By default, display the first tab (flashcards)
    document.getElementById("flashcards").style.display = "block";

    // Tab navigation logic
    function openTab(evt, tabName) {
        var i, tabcontent, tablinks;

        // Hide all tab content
        tabcontent = document.getElementsByClassName("tab-content");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }

        // Remove active class from all tab links
        tablinks = document.getElementsByClassName("tab-link");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        // Show the current tab and add active class to clicked tab
        document.getElementById(tabName).style.display = "block";
        evt.currentTarget.className += " active";
    }

    // Attach the openTab function to the global scope if needed
    window.openTab = openTab;
});

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
        console.log('Login response:', result);  // Log full response for debugging

        if (response.ok) {
            console.log('Points from backend:', result.points);  // Log points specifically

            // Store essential user data in localStorage
            localStorage.setItem('authToken', 'someAuthToken');  // Replace with a real token if needed
            localStorage.setItem('userName', result.name);       // Store the user's name
            localStorage.setItem('userPoints', result.points);   // Store the user's points
            localStorage.setItem('userEmail', result.email);     // Store the user's email for later use

            console.log('User email, name, and points stored in localStorage:', result.email, result.name, result.points);

            alert('Login successful! Welcome, ' + result.name + '. Points: ' + result.points);
            window.location.href = '/';  // Redirect to homepage
        } else {
            alert('Login failed: ' + result.message);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred during login.');
    }
}

// Function to fetch and display user points
function displayUserPoints() {
    const userPoints = localStorage.getItem('userPoints');  // Fetch points from localStorage
    const pointsElement = document.getElementById('user-points');  // The element to display points

    // Debug log for points and DOM element
    console.log('Retrieved userPoints from localStorage:', userPoints);
    console.log('Points element found in DOM:', pointsElement);

    if (pointsElement) {
        if (userPoints) {
            console.log('Setting userPoints in HTML:', userPoints);  // Log the points being set
            pointsElement.textContent = userPoints;  // Display points if they exist
        } else {
            console.log('No userPoints found, setting default 0');  // Log if no points found
            pointsElement.textContent = '0';  // Default to 0 if points are not available
        }
    } else {
        console.error('Element with id "user-points" not found.');  // Log error if element is missing
    }
}
document.addEventListener('DOMContentLoaded', function() {
    displayUserPoints();  // Call this function after the page has fully loaded
});

// Handle user sign-up
async function signUpUser() {
    const name = document.getElementById("signup-name").value;
    const email = document.getElementById("signup-email").value;
    const password = document.getElementById("signup-password").value;
    const birth = document.getElementById("signup-birth").value;  // Get the birthdate from the form

    try {
        const response = await fetch('/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                name: name,
                email: email,
                password: password,
                birth: birth  // Add the birthdate to the POST request
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

// Display user's name and login/logout status
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

// Log out the user
function logoutUser() {
    // Clear localStorage
    localStorage.removeItem('authToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userPoints');
    localStorage.removeItem('userEmail');  // Remove email from localStorage

    // Redirect to login page
    alert('You have been logged out.');
    window.location.href = '/login';
}

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

document.addEventListener("DOMContentLoaded", function () {
    const quizContainers = document.querySelectorAll('.quiz-container');
    let currentQuizIndex = 0;
    const totalQuizzes = quizContainers.length;
    const answers = []; // List to store user answers

    // Function to show the current quiz
    function showQuiz(index) {
        if (index < 0 || index >= totalQuizzes) return;

        quizContainers.forEach((quiz) => quiz.style.display = 'none');
        quizContainers[index].style.display = 'block';
    }

    // Initialize the quiz by showing the first question
    showQuiz(currentQuizIndex);

    // Function to verify the answer with the server and show feedback
    async function verifyAnswer(quizId, selectedAnswer) {
        const response = await fetch('/quizzes/verifyAnswer', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quizId, selectedAnswer })
        });

        const result = await response.json();
        if (result.isCorrect) {
            alert('Correct answer!');
        } else {
            alert('Wrong answer.');
        }

        // Automatically move to the next quiz
        moveToNextQuiz();
    }

    // Function to move to the next quiz
    function moveToNextQuiz() {
        if (currentQuizIndex < totalQuizzes - 1) {
            currentQuizIndex++;
            showQuiz(currentQuizIndex);
        } else {
            console.log("Quiz finished!");
            // Call function to submit all answers or finalize the game
        }
    }

    // Function to handle answer selection and trigger verification
    function handleAnswerSelection(event) {
        const quizContainer = event.target.closest('.quiz-container');
        const quizId = quizContainer.getAttribute('data-quiz-id');
        const selectedAnswer = event.target.value; // The value of the selected answer

        // Store the selected answer
        answers.push({
            quizId: quizId,
            selectedAnswer: selectedAnswer
        });

        // Call function to verify the answer with the server
        verifyAnswer(quizId, selectedAnswer);
    }

    // Add event listener to all clickable options
    quizContainers.forEach((quiz) => {
        quiz.querySelectorAll('.clickable-option').forEach((clickableOption) => {
            clickableOption.addEventListener('click', function () {
                // Find the radio input and select it
                const radioInput = this.querySelector('input[type="radio"]');
                radioInput.checked = true;

                // Add selected class for visual feedback
                quiz.querySelectorAll('.clickable-option').forEach(opt => opt.classList.remove('selected'));
                this.classList.add('selected');

                // Handle answer selection
                handleAnswerSelection(radioInput);
            });
        });
    });
});

// ============================
//  Document Ready Initialization
// ============================
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded');

    // Initialize startup animation
    initStartupAnimation();

    // Display user name and login status
    displayUserNameAndLoginStatus();

    // Check if we're on the challenge page, and if so, display user points
    if (document.getElementById('user-points')) {
        displayUserPoints();
    }

    // Setup event listeners for challenge access
    const challengeLink = document.getElementById('challenge-link');
    if (challengeLink) {
        challengeLink.addEventListener('click', function(event) {
            event.preventDefault();
            checkLoginBeforeChallenge();
        });
    }

    // Initialize flashcards if needed
    initFlashcards();

    // Initialize quiz if needed
    initQuiz();
    initQuizTimer();
    initShowAnswers();
});

// ============================
//  Game Card Answer Check
// ============================
document.addEventListener("DOMContentLoaded", function () {
    const cards = document.querySelectorAll('.card');
    let currentCardIndex = 0;
    const answers = [];
    
    // Function to update the card stack positions
    const updateCardStack = () => {
        cards.forEach((card, index) => {
            const relativeIndex = (index - currentCardIndex + cards.length) % cards.length;
            card.style.zIndex = cards.length - relativeIndex;

            if (relativeIndex === 0) {
                card.style.transform = 'translateY(0px) scale(1)';
                card.style.opacity = 1;
            } else {
                card.style.opacity = 0; // Hide remaining cards
            }
        });
    };

    // Fetch all quizzes and store them in localStorage
	fetch('/quizzes/fetchAll')
	    .then(response => response.json())
	    .then(data => {
	        // Convert ID to string before storing in localStorage
	        data.forEach(quiz => quiz.id = String(quiz.id));  // Ensure id is stored as a string
	        localStorage.setItem('quizzes', JSON.stringify(data));
	    })
	    .catch(error => console.error('Error fetching quizzes:', error));

    // Initially set the correct card positions
    updateCardStack();

    document.querySelectorAll('.next-button').forEach(button => {
        button.addEventListener('click', () => {
            const currentCard = cards[currentCardIndex];
            const quizId = currentCard.getAttribute('data-quiz-id');
            const selectedAnswer = currentCard.querySelector('input[type="radio"]:checked');

            if (selectedAnswer) {
                const userAnswer = {
                    quizId: quizId,
                    selectedAnswer: selectedAnswer.value
                };

                // Verify the answer locally
                verifyAnswerLocally(userAnswer, function(isCorrect) {
                    if (isCorrect) {
                        alert("Correct Answer!");
                    } else {
                        alert("Incorrect Answer!");
                    }

                    // Move to the next card after showing feedback
                    currentCardIndex++;
                    if (currentCardIndex < cards.length) {
                        updateCardStack();
                    } else {
                        console.log("All questions answered.");
                    }
                });

                // Store the answer for later processing
                answers.push(userAnswer);
            }
        });
    });

    // Local verification function
    function verifyAnswerLocally(userAnswer, callback) {
        // Get quizzes from localStorage
        const quizzes = JSON.parse(localStorage.getItem('quizzes'));
        const quiz = quizzes.find(q => q.id === userAnswer.quizId);

        if (quiz) {
            const correctAnswer = quiz.correctAnswerText.trim();
            const isCorrect = userAnswer.selectedAnswer === correctAnswer;
            callback(isCorrect);
        } else {
            console.error('Quiz not found locally for quizId:', userAnswer.quizId);
            callback(false);
        }
    }
});

