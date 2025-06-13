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

    public void sendWelcomeEmail(String to, String fullName, String username, String password) {
        String subject = "Bienvenue - Vos informations de connexion";

        String text = String.format(
                "Bonjour %s,\n\n" +
                        "Votre compte a été créé avec succès par l'administrateur.\n\n" +
                        "Voici vos informations de connexion :\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "📧 Email : %s\n" +
                        "👤 Nom d'utilisateur : %s\n" +
                        "🔑 Mot de passe : %s\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Vous pouvez maintenant vous connecter à la plateforme avec ces informations.\n\n" +
                        "🔗 Lien de connexion : http://localhost:4200/login\n\n" +
                        "⚠️ Pour votre sécurité, nous vous recommandons de changer votre mot de passe lors de votre première connexion.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe AGILIS",
                fullName, to, username, password
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

}


