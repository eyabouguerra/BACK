package backAgil.example.back.controllers;
import backAgil.example.back.models.PasswordResetToken;
import backAgil.example.back.models.RegisterRequest;
import backAgil.example.back.models.Role;
import backAgil.example.back.repositories.PasswordResetTokenRepository;
import backAgil.example.back.repositories.RoleRepository;
import backAgil.example.back.repositories.UserRepository;
import backAgil.example.back.services.EmailService;
import backAgil.example.back.services.PasswordResetTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backAgil.example.back.models.User;
import backAgil.example.back.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  // À définir comme bean dans ta config

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Erreur d’envoi : cet email n’existe pas."));
        }


        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS)); // 1h de validité

        passwordResetTokenService.createOrUpdateToken(user.getUserName(), token, expiryDate);

        emailService.sendResetEmail(user.getEmail(), token);

        return ResponseEntity.ok(Map.of("message", "Un lien de réinitialisation a été envoyé à votre email."));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès."));

    }



    @PostConstruct
    public void initRolesAndUser() {
        userService.initRolesAndUser();
    }



    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    //@PreAuthorize("hasRole('Admin')")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        System.out.println("Received request: " + request);
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Un utilisateur avec cet email existe déjà.");
            }
            if (userRepository.existsByUserName(request.getUserName())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Un utilisateur avec ce nom existe déjà.");
            }

            User user = new User();
            user.setUserName(request.getUserName());
            user.setUserFirstName(request.getUserFirstName());
            user.setUserLastName(request.getUserLastName());
            user.setEmail(request.getEmail());
            user.setUserPassword(request.getUserPassword());

            Set<Role> userRoles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                System.out.println("Looking for role: " + roleName);
                Role role = roleRepo.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
                userRoles.add(role);
            }
            user.setRole(userRoles);

            // Sauvegarder le mot de passe avant chiffrement pour l'email
            String plainPassword = request.getUserPassword();

            User registeredUser = userService.register(user);

            // Envoyer l'email avec les informations de connexion
            emailService.sendWelcomeEmail(
                    registeredUser.getEmail(),
                    registeredUser.getUserFirstName() + " " + registeredUser.getUserLastName(),
                    registeredUser.getUserName(),
                    plainPassword
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur créé avec succès. Un email avec les informations de connexion a été envoyé.",
                    "user", registeredUser
            ));
        } catch (RuntimeException e) {
            System.out.println("Exception: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
    }




    @GetMapping("/forAdmin")
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin() {
        return "This URL is only accessible to admin";
    }

    @GetMapping("/forUser")
    @PreAuthorize("hasAnyRole('Admin','Client')")
    public String forUser() {
        return "This URL is only accessible to the user";
    }

    @GetMapping("/forDispatcheur")
    @PreAuthorize("hasAnyRole('Dispatcheur','Admin')")
    public String forDispatcheur() {
        return "This URL is only accessible to dispatcheur";
    }



    @GetMapping("/users/byRole/{roleName}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userName}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> deleteUser(@PathVariable String userName) {
        try {
            userService.deleteUser(userName);
            return ResponseEntity.ok("✅ Utilisateur supprimé avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        }
    }
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('Admin','User','Dispatcheur')")
    public ResponseEntity<User> getCurrentUserProfile(Principal principal) {
        try {
            String username = principal.getName();
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('Admin','User','Dispatcheur')")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody User updatedUser, Principal principal) {
        try {
            String username = principal.getName();
            User user = userService.updateUserProfile(username, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}



