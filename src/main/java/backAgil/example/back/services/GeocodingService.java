package backAgil.example.back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {
    private final String GEOCODING_API_URL = "https://api.opencagedata.com/geocode/v1/json";
    private final String API_KEY = "5624947ac08d4d688f03d826ac9b2a1f";

    public double[] getCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();//Utilisé pour lire et interpréter une réponse JSON (de l’API) sous forme d’objet.
        //construit une URL complète pour appeler l’API d’OpenCage
        String url = UriComponentsBuilder.fromHttpUrl(GEOCODING_API_URL)
                .queryParam("q", address)
                .queryParam("key", API_KEY)
                .queryParam("language", "fr")
                .toUriString();//convertit le tout en URL de type String

        try {
            String response = restTemplate.getForObject(url, String.class);//Envoie la requête HTTP et récupère la réponse JSON sous forme de String.
            JsonNode root = mapper.readTree(response);
            //elle cherche la partie json contient les résultats
            JsonNode results = root.path("results");
            if (results.isEmpty()) {
                throw new RuntimeException("Adresse introuvable !");
            }
            // Récupère la première adresse trouvée et accède à sa géométrie
            JsonNode geometry = results.get(0).path("geometry");
            double lat = geometry.path("lat").asDouble();
            double lng = geometry.path("lng").asDouble();
            //Retourne un tableau contenant les deux valeurs.
            return new double[]{lat, lng};
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel au service de géocodage", e);
        }
    }
}
