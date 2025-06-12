package backAgil.example.back.controllers;

import backAgil.example.back.models.*;
import backAgil.example.back.repositories.CamionRepository;
import backAgil.example.back.repositories.CommandeRepository;
import backAgil.example.back.repositories.LivraisonRepository;
import backAgil.example.back.services.CamionService;
import backAgil.example.back.services.LivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Position;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/livraisons")
public class LivraisonController {
    @Autowired
    private LivraisonService livraisonService;
    @Autowired
    private CamionService camionService;
    @Autowired
    private CamionRepository camionRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private LivraisonRepository livraisonRepository;
    @GetMapping
    public ResponseEntity<List<Livraison>> getAllLivraisons() {
        List<Livraison> livraisons = livraisonService.getAllLivraisons();
        return ResponseEntity.ok(livraisons);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Livraison> getLivraisonById(@PathVariable Long id) {
        Optional<Livraison> livraison = livraisonService.getLivraisonById(id);
        return livraison.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/user/{username}")
    public List<Livraison> getLivraisonsByUser(@PathVariable String username) {
        return livraisonService.getLivraisonsByUser(username);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Livraison> updateLivraison(@PathVariable Long id, @RequestBody Livraison updatedLivraison) {
        try {
            Livraison livraison = livraisonService.updateLivraison(id, updatedLivraison);
            return ResponseEntity.ok(livraison);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retourne Not Found si la mise à jour échoue
        }
    }

    @PostMapping
    public ResponseEntity<Livraison> createLivraison(@RequestBody Livraison livraison) {
        Livraison newLivraison = livraisonService.addLivraison(livraison);
        return ResponseEntity.status(201).body(newLivraison);
    }
    // CHECK if codeCommande exists.filter(camion -> !camionsUtilises.contains(camion))
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeCommande(@RequestParam String codeLivraison) {
        boolean exists = livraisonRepository.existsByCodeLivraison(codeLivraison);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    @GetMapping("/camions/disponibles")
    public List<Camion> getCamionsDisponibles(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return livraisonService.getCamionsDisponiblesPourDate(date);
    }
    @GetMapping("/position/{commandeId}")
    public ResponseEntity<Map<String, Double>> getLivreurPosition(@PathVariable Long commandeId) {
        // Récupérer la commande par son ID
        Optional<Commande> optionalCommande = commandeRepository.findById(commandeId);
        if (optionalCommande.isEmpty()) {
            return ResponseEntity.notFound().build(); // commande introuvable
        }

        Commande commande = optionalCommande.get();

        // Récupérer le client lié à la commande
        Client client = commande.getClient();
        if (client == null) {
            return ResponseEntity.notFound().build(); // pas de client associé
        }

        // Extraire les coordonnées du client
        Double lat = client.getLatitude();
        Double lng = client.getLongitude();

        if (lat == null || lng == null) {
            return ResponseEntity.noContent().build(); // coordonnées absentes
        }

        // Préparer la réponse
        Map<String, Double> coords = new HashMap<>();
        coords.put("lat", lat);
        coords.put("lng", lng);

        return ResponseEntity.ok(coords);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivraison(@PathVariable Long id) {
        livraisonService.deleteLivraison(id);
        return ResponseEntity.noContent().build(); // Retourne un code 204 si la suppression est réussie
    }
    @GetMapping("/citerne/disponibles")
    public List<Citerne> getCiterneDisponibles(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return livraisonService.getCiterneDisponiblesPourDate(date);
    }








}
