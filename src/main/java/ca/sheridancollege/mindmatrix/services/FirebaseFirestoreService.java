package ca.sheridancollege.mindmatrix.services;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseFirestoreService {

    public String saveUser(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", "");
        userData.put("rank", "");
        userData.put("status", "");
        userData.put("title", "");
        userData.put("birth", "");

        DocumentReference docRef = db.collection("users").document(email);
        WriteResult result = docRef.set(userData).get();

        return "User saved with Firestore at: " + result.getUpdateTime();
    }
}
