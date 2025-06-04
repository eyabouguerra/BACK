package backAgil.example.back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {
    private final String GEOCODING_API_URL = "https://api.opencagedata.com/geocode/v1/json";
    private final String API_KEY = "5624947ac08d4d688f03d826ac9b2a1f"; // Remplace par ta vraie clé

    public double[] getCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String url = UriComponentsBuilder.fromHttpUrl(GEOCODING_API_URL)
                .queryParam("q", address)
                .queryParam("key", API_KEY)
                .queryParam("language", "fr")
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            JsonNode results = root.path("results");
            if (results.isEmpty()) {
                throw new RuntimeException("Adresse introuvable !");
            }

            JsonNode geometry = results.get(0).path("geometry");
            double lat = geometry.path("lat").asDouble();
            double lng = geometry.path("lng").asDouble();

            return new double[]{lat, lng};
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel au service de géocodage", e);
        }
    }
}
