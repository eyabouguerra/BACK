package backAgil.example.back.controllers;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.services.CiterneService;
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

    @Autowired
    private CiterneRepository citerneRepository;
    @Autowired
    private CiterneService citerneService;

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
    public ResponseEntity<List<Compartiment>> getCompartimentsByCiterneId(@PathVariable Long citerneId) {
        try {
            List<Compartiment> compartiments = compartimentService.getCompartimentsByCiterneId(citerneId);
            return ResponseEntity.ok(compartiments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Erreur côté client
        }
    }

    @PostMapping
    public ResponseEntity<Compartiment> createCompartiment(@RequestBody Compartiment compartiment) {
        try {
            Compartiment createdCompartiment = compartimentService.addCompartiment(compartiment);
            return ResponseEntity.ok(createdCompartiment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);  // Erreur côté client
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Erreur interne
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<Compartiment> updateCompartiment(@PathVariable Long id, @RequestBody Compartiment compartiment) {
        try {
            Compartiment updatedCompartiment = compartimentService.updateCompartiment(id, compartiment);
            return ResponseEntity.ok(updatedCompartiment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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

    @PostMapping("/{id}/add-compartiment")
    public ResponseEntity<Compartiment> addCompartimentToCiterne(
            @PathVariable Long id,
            @RequestBody Compartiment compartiment
    ) {
        Compartiment result = citerneService.addCompartimentToCiterne(id, compartiment);
        return ResponseEntity.ok(result);
    }



}
