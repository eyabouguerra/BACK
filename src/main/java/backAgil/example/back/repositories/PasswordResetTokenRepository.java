package backAgil.example.back.repositories;
import backAgil.example.back.models.PasswordResetToken;
import backAgil.example.back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser_UserName(String userName);
    void deleteByUser(User user);
}

