package backAgil.example.back.controllers;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.repositories.CompartimentRepository;
import backAgil.example.back.services.CiterneService;
import backAgil.example.back.services.CompartimentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Ajout d'une route pour récupérer les compartiments d'une citerne spécifique
    @GetMapping("/{id}/compartiments")
    public ResponseEntity<List<Compartiment>> getCompartimentsByCiterneId(@PathVariable Long id) {
        Optional<Citerne> citerne = citerneService.getCiterneById(id);
        if (citerne.isPresent()) {
            List<Compartiment> compartiments = compartimentService.getCompartimentsByCiterneId(id);
            return ResponseEntity.ok(compartiments);
        } else {
            return ResponseEntity.notFound().build(); // Citerne non trouvée
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Citerne> getCiterneById(@PathVariable Long id) {
        Optional<Citerne> citerne = citerneService.getCiterneById(id);
        if (citerne.isPresent()) {
            return ResponseEntity.ok(citerne.get());
        } else {
            return ResponseEntity.notFound().build(); // Citerne non trouvée
        }
    }


    @PostMapping()
    public ResponseEntity<Citerne> addCiterne(@RequestBody Citerne citerne) {
        List<Compartiment> compartiments = citerne.getCompartiments();

        // Nettoyer la liste pour éviter des erreurs de persistance
        citerne.setCompartiments(new ArrayList<>());

        // Sauvegarder la citerne seule d’abord pour qu’elle ait un ID
        Citerne savedCiterne = citerneRepository.save(citerne);

        // Lier chaque compartiment à la nouvelle citerne
        for (Compartiment c : compartiments) {
            Compartiment existing = compartimentRepository.findById(c.getId()).orElse(null);
            if (existing != null) {
                existing.setCiterne(savedCiterne);
                compartimentRepository.save(existing);
            }
        }

        // Optionnel : récupérer la citerne avec les compartiments mis à jour
        savedCiterne.setCompartiments(compartimentRepository.findByCiterneId(savedCiterne.getId()));

        return ResponseEntity.ok(savedCiterne);
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


    @Transactional
    public Compartiment addCompartimentToCiterne(Long citerneId, Compartiment compartiment) {
        Citerne citerne = citerneRepository.findById(citerneId)
                .orElseThrow(() -> new IllegalArgumentException("Citerne not found"));

        // Vérifier si le compartiment est détaché (n'a pas d'ID)
        if (compartiment.getId() == null || !compartimentRepository.existsById(compartiment.getId())) {
            // Si c'est un nouveau compartiment, il doit être sauvegardé dans la base
            compartiment = compartimentRepository.save(compartiment);
        } else {
            // Si le compartiment est déjà persistant, il faut s'assurer qu'il est attaché à la session
            compartiment = entityManager.merge(compartiment); // Merge au lieu de saveAndFlush
        }

        // Maintenant, lier le compartiment à la citerne
        compartiment.setCiterne(citerne);

        // Sauvegarder à nouveau le compartiment avec sa citerne associée
        return compartimentRepository.save(compartiment);
    }




}
