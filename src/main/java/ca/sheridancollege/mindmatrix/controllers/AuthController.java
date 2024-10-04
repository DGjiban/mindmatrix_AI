package ca.sheridancollege.mindmatrix.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import ca.sheridancollege.mindmatrix.beans.User;
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
            @RequestParam String name,
            @RequestParam String birth) {
        
        try {
            // Create the user in Firebase Authentication
            UserRecord userRecord = firebaseAuthService.createUser(email, password);
            
            // Save the user details (including name) in Firestore
            firebaseFirestoreService.saveUser(email, name, birth);
            
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
            User currentUser = firebaseFirestoreService.getUserNameByEmail(email);
            System.out.println("User points: " + currentUser.getPoints());  // Log points for debugging

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("email", user.getEmail());
            response.put("name", currentUser.getName());
            response.put("points", currentUser.getPoints());  // Ensure points are included in the response

            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body(null);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    // New method to fetch the current user's points
    @GetMapping("/points")
    public ResponseEntity<Integer> getUserPoints(@RequestParam String email) {
        try {
            int points = firebaseFirestoreService.getUserPointsByEmail(email);
            return ResponseEntity.ok(points);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body(0);  // Return 0 points if there's an error
        }
    }
}

