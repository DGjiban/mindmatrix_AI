package ca.sheridancollege.mindmatrix.controllers;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<String> signUp(
            @RequestParam String email, 
            @RequestParam String password, 
            @RequestParam String name) {  // Accept "name" from the signup form
        
        try {
            // Create the user in Firebase Authentication
            UserRecord userRecord = firebaseAuthService.createUser(email, password);
            
            // Save the user details (including name) in Firestore
            firebaseFirestoreService.saveUser(email, name);
            
            return ResponseEntity.ok("User created with ID: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body("Error creating user: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String email, @RequestParam String password) {
        try {
            UserRecord user = firebaseAuthService.getUserByEmail(email);

            // Fetch user data from Firestore
            String name;
            
            try {
                name = firebaseFirestoreService.getUserNameByEmail(email).getName();
            } catch (InterruptedException | ExecutionException e) {
                name = "Unknown User";  // Fallback if something goes wrong
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("email", user.getEmail());
            response.put("name", name);  // Include name in response
            
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body(null);
        }
    }
    
}
