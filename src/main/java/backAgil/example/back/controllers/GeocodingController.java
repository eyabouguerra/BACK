package backAgil.example.back.controllers;

import backAgil.example.back.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/geocode")
@CrossOrigin(origins = "*")
public class GeocodingController {

    @Autowired
    private GeocodingService geocodingService;

    @GetMapping
    public Map<String, Double> getCoordinates(@RequestParam String address) {
        double[] coords = geocodingService.getCoordinates(address);

        Map<String, Double> response = new HashMap<>();
        response.put("latitude", coords[0]);
        response.put("longitude", coords[1]);

        return response;
    }
}
