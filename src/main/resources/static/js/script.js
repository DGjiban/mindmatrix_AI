window.onload = function() {
    const startup = document.getElementById('startup');
    const mainContent = document.getElementById('main-content');

    function onScroll() {
        // Apply styles to fade out the startup section
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

    // Função para atualizar o contador
    function updateQuizCounter() {
        counterElement.textContent = `${currentQuizIndex + 1} of ${totalQuizzes}`;
    }

    // Função para mostrar a questão do quiz atual
    function showQuiz(index) {
        // Esconde todas as questões
        quizContainers.forEach((container, i) => {
            container.style.display = 'none';
        });
        // Mostra a questão atual
        quizContainers[index].style.display = 'block';
        // Atualiza o contador
        updateQuizCounter();
    }

    // Evento de clique para o botão "Previous"
    document.getElementById('QuizprevButton').addEventListener('click', function() {
        if (currentQuizIndex > 0) {
            currentQuizIndex--;
            showQuiz(currentQuizIndex);
        }
    });

    // Evento de clique para o botão "Next"
    document.getElementById('QuiznextButton').addEventListener('click', function() {
        if (currentQuizIndex < totalQuizzes - 1) {
            currentQuizIndex++;
            showQuiz(currentQuizIndex);
        }
    });

    // Inicia o quiz mostrando a primeira questão e inicializando o contador
    showQuiz(currentQuizIndex);
});



function restartQuiz() {
    // Recarregar a página ou redefinir o estado do quiz
    window.location.reload();
}


