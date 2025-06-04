package backAgil.example.back.services;

import backAgil.example.back.models.User;
import java.util.List;

public interface UserService {
    public List<User> getUsersByRole(String roleName);
    void initRolesAndUser();
    List<String> getAllRoles();
    void deleteUser(String userName);
    User register(User user);
    String getEncodedPassword(String password); // ← Optionnelle selon ton besoin
}
