package ca.sheridancollege.mindmatrix.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import javax.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {
	@PostConstruct
    public void initializeFirebase() throws IOException {
        // Load the service account file from the resources folder
        ClassPathResource resource = new ClassPathResource("mindmatrix-b1530-firebase-adminsdk-vhm0w-4978435075.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
	
	@Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
