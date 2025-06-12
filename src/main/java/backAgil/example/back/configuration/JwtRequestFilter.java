package backAgil.example.back.configuration;

import backAgil.example.back.servicesImpl.JwtService;
import backAgil.example.back.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ✅ Exclure les chemins publics du filtre
        String path = request.getServletPath();
        System.out.println("Request path: " + path);
        if (path.equals("/register") || path.equals("/authenticate") || path.equals("/roles")) {
            System.out.println("Excluding from JWT filter: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader("Authorization"); //Elle récupère le token JWT
        String jwtToken = null;
        String userName = null;

        if (header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7);
            try {
                userName = jwtUtil.getUsernameFromToken(jwtToken);//Elle lit dans le token pour savoir à quel utilisateur il appartient.
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token is expired");
            }
        } else {
            System.out.println("JWT token does not start with Bearer ");
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtService.loadUserByUsername(userName);

            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}
