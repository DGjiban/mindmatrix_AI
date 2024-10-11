package ca.sheridancollege.mindmatrix.services;

import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            // Fetch the points field as an Object, which could be a String
            String pointsString = document.getString("points");

            if (pointsString != null) {
                try {
                    // Convert the points from String to Integer
                    return Integer.parseInt(pointsString);
                } catch (NumberFormatException e) {
                    // Log the error and return a default value (e.g., 0) if parsing fails
                    e.printStackTrace();
                    return 0;
                }
            } else {
                // Return 0 if points field is null
                return 0;
            }
        } else {
            // Return 0 if the document does not exist
            return 0;
        }
    }



    // Optionally, add a method to update points
    public String updateUserPoints(String email, String points) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(email);

        Map<String, Object> updates = new HashMap<>();
        updates.put("points", points);  // Store points as a string

        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        return "User points updated at: " + writeResult.get().getUpdateTime();
    }


    
 // Method to fetch the total number of questions from the "questions" collection
    public int getTotalQuestions() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference questionsCollection = db.collection("quizzes");

        // Get all documents in the collection
        ApiFuture<QuerySnapshot> querySnapshot = questionsCollection.get();

        // Return the total number of documents (questions)
        return querySnapshot.get().size();
    }

    public List<User> getRankedUsersByPoints() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersCollection = db.collection("users");

        // Fetch all user documents
        ApiFuture<QuerySnapshot> querySnapshot = usersCollection.get();

        List<User> users = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            User user = document.toObject(User.class);

            // Handle potential null or empty points
            String pointsString = document.getString("points");
            int points = 0;  // Default to 0 if points are not valid

            if (pointsString != null && !pointsString.trim().isEmpty()) {
                try {
                    points = Integer.parseInt(pointsString);  // Convert to integer if possible
                } catch (NumberFormatException e) {
                    System.err.println("Invalid points value for user: " + user.getName());
                }
            }

            user.setPoints(Integer.toString(points));  // Ensure points are stored back as a string
            users.add(user);
        }

        // Sort users by points in descending order
        users.sort((u1, u2) -> Integer.compare(Integer.parseInt(u2.getPoints()), Integer.parseInt(u1.getPoints())));

        return users;
    }
    
    
    public int getUserRankByEmail(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersCollection = db.collection("users");

        // Fetch all users and their points
        ApiFuture<QuerySnapshot> querySnapshot = usersCollection.get();
        List<User> users = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            User user = document.toObject(User.class);
            if (user.getPoints() != null) {
                try {
                    user.setPoints(String.valueOf(Integer.parseInt(user.getPoints())));
                } catch (NumberFormatException e) {
                    user.setPoints("0");
                }
                users.add(user);
            }
        }

        // Sort users by points in descending order
        users.sort((u1, u2) -> Integer.compare(Integer.parseInt(u2.getPoints()), Integer.parseInt(u1.getPoints())));

        // Find the rank of the user with the given email
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                return i + 1;  // Return rank (1-based index)
            }
        }

        return -1;  // Return -1 if the user is not found
    }


    
}
