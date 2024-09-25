package ca.sheridancollege.mindmatrix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import ca.sheridancollege.mindmatrix.services.FirebaseAuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private FirebaseAuthService firebaseAuthService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestParam String email, @RequestParam String password) {
        try {
            UserRecord userRecord = firebaseAuthService.createUser(email, password);
            return ResponseEntity.ok("User created with ID: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            UserRecord user = firebaseAuthService.getUserByEmail(email);
            // For real-world applications, you would verify the password and return a token
            return ResponseEntity.ok("Login successful for user: " + user.getEmail());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body("Login failed: " + e.getMessage());
        }
    }
    
    
}
