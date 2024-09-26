package ca.sheridancollege.mindmatrix.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import ca.sheridancollege.mindmatrix.services.FirebaseAuthService;
import ca.sheridancollege.mindmatrix.services.FirebaseFirestoreService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private FirebaseAuthService firebaseAuthService;
	
	@Autowired
    private FirebaseFirestoreService firebaseFirestoreService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestParam String email, @RequestParam String password) throws InterruptedException, ExecutionException {
    	try {
    		
            UserRecord userRecord = firebaseAuthService.createUser(email, password);
            
            firebaseFirestoreService.saveUser(email);
            
            return ResponseEntity.ok("User created with ID: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body("Error saving user : " + e.getMessage());
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
