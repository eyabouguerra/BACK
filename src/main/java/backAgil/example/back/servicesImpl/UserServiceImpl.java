package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Role;
import backAgil.example.back.models.User;
import backAgil.example.back.repositories.RoleRepository;
import backAgil.example.back.repositories.UserRepository;
import backAgil.example.back.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
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

        User user = new User();
        user.setUserFirstName("raj");
        user.setUserLastName("sharma");
        user.setUserName("raj123");
        user.setUserPassword(getEncodedPassword("raj@pass"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setRole(userRoles);
        userRepo.save(user);
    }
public List<User> getUsersByRole(String roleName) {
    if (!roleRepo.existsById(roleName)) {
        throw new RuntimeException("Role not found: " + roleName);
    }
    return userRepo.findByRoleName(roleName);
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
