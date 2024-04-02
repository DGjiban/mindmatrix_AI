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

//timer
const startingMinutes = 10;
let time = startingMinutes * 60;

const timerEl = document.getElementById('timer');
setInterval(updateTimer, 1000);

function updateTimer()
{
	const minutes = Math.floor(time / 60);
	let seconds = time % 60;
	timerEl.innerHTML = `${minutes}:${seconds}`;
	
	time--;
}