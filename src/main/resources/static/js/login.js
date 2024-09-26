/* ============================
   Login / Sign-Up Logic 
============================ */

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

// Function to handle user login
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

        const result = await response.text();
        if (response.ok) {
            alert('Login successful! Token: ' + result);
            localStorage.setItem('authToken', result);  // Store the token
            window.location.href = '/';  // Redirect to homepage
        } else {
            alert('Login failed: ' + result);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred during login.');
    }
}

// Function to handle user sign-up
async function signUpUser() {
    const email = document.getElementById("signup-email").value;
    const password = document.getElementById("signup-password").value;

    try {
        const response = await fetch('/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ email: email, password: password })
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

// Initialize the page with login form visible by default
window.onload = function() {
    if (document.getElementById("login-section")) {
        showLoginForm();
    }
};