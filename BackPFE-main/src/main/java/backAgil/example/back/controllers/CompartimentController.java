package backAgil.example.back.controllers;

import backAgil.example.back.models.Compartiment;
import backAgil.example.back.services.CompartimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/compartiments")
public class CompartimentController {

    @Autowired
    private CompartimentService compartimentService;

    @GetMapping
    public List<Compartiment> getAllCompartiments() {
        return compartimentService.getAllCompartiments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compartiment> getCompartimentById(@PathVariable Long id) {
        Optional<Compartiment> compartiment = compartimentService.getCompartimentById(id);
        return compartiment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/citerne/{citerneId}")
    public List<Compartiment> getCompartimentsByCiterneId(@PathVariable Long citerneId) {
        return compartimentService.getCompartimentsByCiterneId(citerneId);
    }


    @PostMapping
    public ResponseEntity<?> addCompartiment(@RequestBody Compartiment compartiment) {
        try {
            if (compartiment.getCiterne() != null && compartiment.getCiterne().getId() == null) {
                compartiment.setCiterne(null);  // Si citerneId est nul, on la supprime.
            }
            // Appel à la logique de service pour ajouter un compartiment
            Compartiment savedCompartiment = compartimentService.addCompartiment(compartiment);
            return ResponseEntity.ok(savedCompartiment); // Retourner le compartiment ajouté
        } catch (IllegalArgumentException e) {
            // Retourner une erreur 400 si la validation échoue
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            // En cas d'erreur inconnue, retourner une erreur générique
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne");
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<Compartiment> updateCompartiment(@PathVariable Long id, @RequestBody Compartiment compartiment) {
        try {
            Compartiment updatedCompartiment = compartimentService.updateCompartiment(id, compartiment);
            return ResponseEntity.ok(updatedCompartiment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompartiment(@PathVariable Long id) {
        try {
            compartimentService.deleteCompartiment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}