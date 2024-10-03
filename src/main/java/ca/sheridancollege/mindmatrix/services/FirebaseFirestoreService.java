package ca.sheridancollege.mindmatrix.services;

import com.google.cloud.firestore.Firestore;
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

    public String saveUser(String email, String name) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("points", "");
        userData.put("birth", "");

        DocumentReference docRef = db.collection("users").document(email);
        WriteResult result = docRef.set(userData).get();

        return "User saved with Firestore at: " + result.getUpdateTime();
    }
    
    public User getUserNameByEmail(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
       
        DocumentReference docRef = db.collection("users").document(email);
        DocumentSnapshot document = docRef.get().get();
        
        if (document.exists()) {
        User currentUser = new User();
        currentUser.setEmail(email);
        currentUser.setName(document.getString("name"));
        currentUser.setPoints(document.getString("points"));  // Assuming points are stored as a string
        currentUser.setBirth(document.getString("birth"));
        	
        return currentUser;
        	
        } else {
        	 return new User(email, "User", "", "");
        }
    }
}
