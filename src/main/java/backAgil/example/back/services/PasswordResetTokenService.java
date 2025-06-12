package backAgil.example.back.services;

import backAgil.example.back.models.PasswordResetToken;
import backAgil.example.back.repositories.PasswordResetTokenRepository;
import backAgil.example.back.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;//manipuler les jetons de réinitialisation
    @Autowired
    private UserRepository userRepository;//chercher un utilisateur avec son nom.
    public void createOrUpdateToken(String userName, String token, Date expiryDate) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByUser_UserName(userName);// Cherche si token existe déjà pour cet utilisateur.


        if (tokenOpt.isPresent()) {//Mets à jour le jeton et sa date d’expiration puis l’enregistre
            PasswordResetToken existingToken = tokenOpt.get();
            existingToken.setToken(token);
            existingToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            passwordResetTokenRepository.save(existingToken);
        } else {//Crée un nouveau jeton
            PasswordResetToken newToken = new PasswordResetToken();
            newToken.setUser(userRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found")));//Associe-le à un utilisateur trouvé dans la base.
            newToken.setToken(token);
            newToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());//Définit le jeton et la date d’expiration.
            passwordResetTokenRepository.save(newToken);
        }
    }

}

