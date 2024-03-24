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
    const flashcards = document.querySelectorAll('.flashcard-container .flashcard');

    flashcards.forEach(flashcard => {
        const question = flashcard.querySelector('.flashcard-front h2');
        const answer = flashcard.querySelector('.flashcard-back p');

        adjustFontSizeBasedOnContentLength(question);
        adjustFontSizeBasedOnContentLength(answer);
    });

    function adjustFontSizeBasedOnContentLength(element) {
        if (!element) return; // If the element doesn't exist, do nothing

        let contentLength = element.textContent.length;

        if (contentLength > 100) { // Adjust these values based on your needs
            element.style.fontSize = '14px'; // Smaller font size for longer content
        } else if (contentLength > 50) {
            element.style.fontSize = '16px'; // Medium font size
        } else {
            element.style.fontSize = '18px'; // Larger font size for shorter content
        }
    }
});