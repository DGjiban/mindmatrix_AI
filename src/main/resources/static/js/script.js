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
document.querySelectorAll('.flashcard-container').forEach(container => {
  container.addEventListener('click', function() {
    const flashcard = this.querySelector('.flashcard');
    flashcard.classList.toggle('is-flipped');
  });
});

document.addEventListener("DOMContentLoaded", function() {
    const flashcards = document.querySelectorAll('.flashcard-container');
    let currentIndex = 0;

    // Show the first flashcard initially, hide all others
    showFlashcard(currentIndex);

    function showFlashcard(index) {
        // Hide all flashcards
        flashcards.forEach((flashcard, i) => {
            if (i !== index) {
                flashcard.style.display = 'none';
            } else {
                flashcard.style.display = 'block'; // Show the current flashcard
            }
        });
    }

    // Next button click event
    document.getElementById('nextButton').addEventListener('click', function() {
        currentIndex++;
        if (currentIndex >= flashcards.length) {
            currentIndex = 0; // Wrap around to the first flashcard
        }
        showFlashcard(currentIndex);
    });

    // Previous button click event
    document.getElementById('prevButton').addEventListener('click', function() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = flashcards.length - 1; // Wrap around to the last flashcard
        }
        showFlashcard(currentIndex);
    });
});
