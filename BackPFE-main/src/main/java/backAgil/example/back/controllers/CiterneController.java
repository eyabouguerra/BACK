package backAgil.example.back.controllers;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.repositories.CompartimentRepository;
import backAgil.example.back.services.CiterneService;
import backAgil.example.back.services.CompartimentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/citernes")

@CrossOrigin("*")
public class CiterneController {

    @Autowired
    private CiterneService citerneService;
    @Autowired
    private CiterneRepository citerneRepository;
    @Autowired
    private CompartimentRepository compartimentRepository;
    @Autowired
    private CompartimentService compartimentService;
    @Autowired
    private EntityManager entityManager;


    @GetMapping
    public List<Citerne> getAllCiternes() {
        return citerneService.getAllCiternes();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Citerne> getCiterneById(@PathVariable Long id) {
        Optional<Citerne> citerne = citerneService.getCiterneById(id);
        return citerne.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<?> addCiterne(@RequestBody Citerne citerne) {
        if (citerne.getCompartiments().size() != citerne.getNombreCompartiments()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Le nombre de compartiments doit être exactement " + citerne.getNombreCompartiments());
        }

        List<Compartiment> compartiments = citerne.getCompartiments();

        // Sauvegarde de la citerne et association des compartiments
        Citerne savedCiterne = citerneService.addCiterne(citerne);

        // Lier les compartiments à la citerne
        for (Compartiment compartiment : compartiments) {
            compartiment.setCiterne(savedCiterne);  // Important: Lier chaque compartiment à la citerne
        }

        // Sauvegarder les compartiments après la liaison avec la citerne
        compartimentRepository.saveAll(compartiments);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCiterne);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCiterne(@PathVariable Long id, @RequestBody Citerne citerne) {
        try {
            Citerne updatedCiterne = citerneService.updateCiterne(id, citerne);
            return ResponseEntity.ok(updatedCiterne); // Retourne la citerne mise à jour
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Citerne non trouvée pour l'id : " + id); // Citerne non trouvée
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Erreur de validation
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCiterne(@PathVariable Long id) {
        Optional<Citerne> citerne = citerneService.getCiterneById(id);
        if (citerne.isPresent()) {
            citerneService.deleteCiterne(id);
            return ResponseEntity.noContent().build(); // Suppression réussie
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Citerne non trouvée
        }
    }
}

