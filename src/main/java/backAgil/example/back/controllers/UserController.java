package backAgil.example.back.controllers;
import backAgil.example.back.models.RegisterRequest;
import backAgil.example.back.models.Role;
import backAgil.example.back.repositories.RoleRepository;
import backAgil.example.back.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backAgil.example.back.models.User;
import backAgil.example.back.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin("*")
@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepo;

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
       try {
           User user = new User();
           user.setUserName(request.getUserName());
           user.setUserFirstName(request.getUserFirstName());
           user.setUserLastName(request.getUserLastName());
           user.setEmail(request.getEmail());
           user.setUserPassword(request.getUserPassword());

           Set<Role> userRoles = new HashSet<>();
           for (String roleName : request.getRoles()) {
               Role role = roleRepo.findByRoleName(roleName)
                       .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
               userRoles.add(role);
           }
           // Utiliser le setter au pluriel
           user.setRole(userRoles);

           User registeredUser = userService.register(user);
           return ResponseEntity.ok(registeredUser);

       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
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
    @Autowired
    private UserRepository userRepository;


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

}




