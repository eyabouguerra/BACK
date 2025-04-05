package backAgil.example.back.controllers;

import backAgil.example.back.models.Livraison;
import backAgil.example.back.services.LivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/livraisons")
public class LivraisonController {
    @Autowired
    private LivraisonService livraisonService;

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


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivraison(@PathVariable Long id) {
        livraisonService.deleteLivraison(id);
        return ResponseEntity.noContent().build(); // Retourne un code 204 si la suppression est réussie
    }

    @GetMapping("/immatriculation/{marque}")
    public ResponseEntity<String> getImmatriculationByMarque(@PathVariable String marque) {
        String immatriculation = livraisonService.getImmatriculationByMarque(marque);
        if (immatriculation != null) {
            return ResponseEntity.ok(immatriculation);
        } else {
            return ResponseEntity.notFound().build(); // Retourne Not Found si l'immatriculation n'est pas trouvée
        }
    }


}
