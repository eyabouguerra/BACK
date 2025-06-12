package backAgil.example.back.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String token) {
        String subject = "Réinitialisation du mot de passe";
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        String text = "Cliquez sur le lien pour réinitialiser votre mot de passe : " + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}


