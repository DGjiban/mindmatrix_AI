package ca.sheridancollege.mindmatrix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping("/send-contact")
    public String sendContactEmail(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("anoshabari23.6@gmail.com");
        mailMessage.setSubject("Contact Form Submission");
        mailMessage.setText("Name: " + name + "\nEmail: " + email + "\n\nMessage:\n" + message);

        try {
            emailSender.send(mailMessage);
            return "Message sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send message.";
        }
    }
}
