package ca.sheridancollege.mindmatrix.gpt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GptRequest {

    private String model;
    private List<Message> messages;
    private Integer max_tokens; // New parameter for max_tokens

    public GptRequest(String model, String prompt, Integer max_tokens) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
        this.max_tokens = max_tokens; // Set max_tokens
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
