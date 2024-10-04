package ca.sheridancollege.mindmatrix.services;

import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import ca.sheridancollege.mindmatrix.beans.User;


@Service
public class FirebaseFirestoreService {

    // Save user with birth and default points as 0
    public String saveUser(String email, String name, String birth) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("birth", birth);
        userData.put("points", 0);  // Store points as an integer, default to 0

        DocumentReference docRef = db.collection("users").document(email);
        ApiFuture<WriteResult> result = docRef.set(userData);

        return "User saved with Firestore at: " + result.get().getUpdateTime();
    }

    // Fetch user details by email, including points
    public User getUserNameByEmail(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(email);
        DocumentSnapshot document = docRef.get().get();

        if (document.exists()) {
            User currentUser = new User();
            currentUser.setEmail(email);
            currentUser.setName(document.getString("name"));
            currentUser.setBirth(document.getString("birth"));

            // Safely handle points whether it's stored as a Number or String
            Object pointsField = document.get("points");

            if (pointsField instanceof Long) {
                currentUser.setPoints(String.valueOf(pointsField));  // Convert Long to String
            } else if (pointsField instanceof String) {
                currentUser.setPoints((String) pointsField);  // Already a String, just set it
            } else {
                currentUser.setPoints("0");  // Default value if points are not found or incorrectly stored
            }

            return currentUser;
        } else {
            return new User(email, "Unknown User", "", "0");
        }
    }

    // Fetch only the user's points by email
    public int getUserPointsByEmail(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(email);
        DocumentSnapshot document = docRef.get().get();

        if (document.exists()) {
            Long points = document.getLong("points");  // Retrieve points as Long
            return points != null ? points.intValue() : 0;  // Safely handle null points, return 0 if missing
        } else {
            return 0;  // Return 0 points if user doesn't exist
        }
    }

    // Optionally, add a method to update points
    public String updateUserPoints(String email, int points) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(email);

        Map<String, Object> updates = new HashMap<>();
        updates.put("points", points);  // Only update points

        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        return "User points updated at: " + writeResult.get().getUpdateTime();
    }
}
