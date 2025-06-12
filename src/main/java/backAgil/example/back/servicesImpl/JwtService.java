package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.LoginRequest;
import backAgil.example.back.models.LoginResponse;
import backAgil.example.back.models.User;
import backAgil.example.back.repositories.UserRepository;
import backAgil.example.back.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public LoginResponse createJwtToken(LoginRequest loginRequest) throws Exception {
        String userName = loginRequest.getUserName();
        String userPassword = loginRequest.getUserPassword();
        authenticate(userName, userPassword);

        // Charger le user depuis la base
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + userName));

        // Créer le token JWT
        final UserDetails userDetails = loadUserByUsername(userName);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        return new LoginResponse(user, newGeneratedToken);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getUserPassword(),
                getAuthorities(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthorities(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (user.getRole() != null) {  // <-- pluriel ici
            user.getRole().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
            });
        }
        return authorities;
    }


    private void authenticate(String userName, String userPassword) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException e) {
            throw new Exception("User is disabled");
        } catch (BadCredentialsException e) {
            throw new Exception("Bad credentials from user");
        }
    }
}
