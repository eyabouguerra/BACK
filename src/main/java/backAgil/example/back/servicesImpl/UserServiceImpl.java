package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.PasswordResetToken;
import backAgil.example.back.models.Role;
import backAgil.example.back.models.User;
import backAgil.example.back.repositories.PasswordResetTokenRepository;
import backAgil.example.back.repositories.RoleRepository;
import backAgil.example.back.repositories.UserRepository;
import backAgil.example.back.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {  // <--- ici "getRoles()"
            throw new RuntimeException("At least one role is required");
        }

        // Encodage du mot de passe
        user.setUserPassword(getEncodedPassword(user.getUserPassword()));

        return userRepo.save(user);
    }
    @Transactional
    public User updateUserProfile(String username, User updatedUser) {
        Optional<User> userOpt = userRepo.findById(username);
        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();

            // Mise à jour des informations (sauf username et rôles)
            existingUser.setUserFirstName(updatedUser.getUserFirstName());
            existingUser.setUserLastName(updatedUser.getUserLastName());
            existingUser.setEmail(updatedUser.getEmail());

            // Si un nouveau mot de passe est fourni
            if (updatedUser.getUserPassword() != null && !updatedUser.getUserPassword().isEmpty()) {
                existingUser.setUserPassword(getEncodedPassword(updatedUser.getUserPassword()));
            }

            return userRepo.save(existingUser);
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec le nom : " + username);
        }
    }

    public void requestResetPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        // Supprimer anciens tokens
        tokenRepository.deleteByUser(user);

        // Générer un token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        // Créer lien
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // Envoyer email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Réinitialisation de mot de passe");
        message.setText("Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink);

        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide."));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Le token a expiré.");
        }

        User user = resetToken.getUser();
        user.setUserPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepo.save(user);

        tokenRepository.delete(resetToken);
    }






    public void initRolesAndUser(){
        Role adminRole = new Role();
        adminRole.setRoleName("Admin");
        adminRole.setRoleDescription("Admin role");
        roleRepo.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("User");
        userRole.setRoleDescription("Default role for newly created record");
        roleRepo.save(userRole);

        Role dispatcheurRole = new Role();
        dispatcheurRole.setRoleName("Dispatcheur");
        dispatcheurRole.setRoleDescription("dispacheur role");
        roleRepo.save(dispatcheurRole);

        User adminUser = new User();
        adminUser.setUserFirstName("admin");
        adminUser.setUserLastName("admin");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setUserName("admin123");
        adminUser.setUserPassword(getEncodedPassword("admin@pass"));
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        userRepo.save(adminUser);


    }
public List<User> getUsersByRole(String roleName) {
    if (!roleRepo.existsById(roleName)) {
        throw new RuntimeException("Role not found: " + roleName);
    }
    return userRepo.findByRoleName(roleName);
}

    public User getUserByUsername(String username) {
        Optional<User> userOpt = userRepo.findById(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec le nom : " + username);
        }
    }

    public String getEncodedPassword(String password){
        return passwordEncoder.encode(password);
    }
    public List<String> getAllRoles() {
        return StreamSupport.stream(roleRepo.findAll().spliterator(), false)
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
    public void deleteUser(String userName) {
        Optional<User> userOpt = userRepo.findById(userName);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getRole().clear(); // Supprime les liens avec les rôles
            userRepo.delete(user); // Supprime maintenant sans casser les contraintes
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec le nom : " + userName);
        }
    }



}
