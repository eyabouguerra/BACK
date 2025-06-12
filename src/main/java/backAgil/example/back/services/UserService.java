package backAgil.example.back.services;

import backAgil.example.back.models.User;
import java.util.List;

public interface UserService {
    public List<User> getUsersByRole(String roleName);
    void initRolesAndUser();
    List<String> getAllRoles();
    void deleteUser(String userName);
    User register(User user);
    String getEncodedPassword(String password);
    void requestResetPassword(String email);
    void resetPassword(String token, String newPassword) ;
    public User getUserByUsername(String username);
    User updateUserProfile(String username, User updatedUser);
}
