

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//liaison avec le front

@Configuration
public class CorsConfiguration {


    //creation d'un objet
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // URL de votre frontend Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")//'accepter tous les en-têtes HTTP (headers) dans les requêtes envoyées depuis le frontend
                        .allowCredentials(true)//permet aux requêtes d'inclure des informations d'identification, comme les cookies, les jetons d'authentification.....
                        .maxAge(3600); // durée de validité des OPTIONS en secondes
            }
        };
    }
}