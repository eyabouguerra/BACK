package backAgil.example.back.controllers;
import backAgil.example.back.models.CommandeProduit;
import backAgil.example.back.models.Commande;
import backAgil.example.back.models.Produit;
import backAgil.example.back.models.TypeProduit;
import backAgil.example.back.repositories.*;
import backAgil.example.back.services.CommandeService;
import backAgil.example.back.services.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/commandes/v1")
@CrossOrigin("*")
public class CommandeController {

    @Autowired
    private CommandeService cService;

    @Autowired
    private ProduitService produitService;
    @Autowired
    private commandeProduitRepository  commandeProduitRepository;

    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private TypeProduitRepository typeProduitRepository;

    // GET all commandes
    @GetMapping
    public List<Commande> getAll() {
        return cService.getAllCommandes();
    }

    // GET commande by ID
    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable("id") Long id) {
        return commandeRepository.findCommandeWithProduitsAndTypes(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CHECK if codeCommande exists
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeCommande(@RequestParam String codeCommande) {
        boolean exists = commandeRepository.existsByCodeCommande(codeCommande);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // GET commandes by status
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Commande>> getCommandesByStatut(@PathVariable("statut") String statut) {
        try {
            Commande.StatutCommande statutCommande = Commande.StatutCommande.valueOf(statut.toUpperCase());
            List<Commande> commandes = cService.getCommandesByStatut(statutCommande);
            return ResponseEntity.ok(commandes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // UPDATE commande status
    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateCommandeStatut(@PathVariable("id") Long id, @RequestBody Map<String, String> statutMap) {
        try {
            String statutStr = statutMap.get("statut");
            if (statutStr == null) {
                return ResponseEntity.badRequest().body("Le statut est requis");
            }

            Commande.StatutCommande nouveauStatut = Commande.StatutCommande.valueOf(statutStr.toUpperCase());
            Commande updatedCommande = cService.updateStatutCommande(id, nouveauStatut);
            return ResponseEntity.ok(updatedCommande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Statut invalide. Valeurs acceptées: EN_COURS, PLANNIFIER, LIVRE");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    // GET all possible statuses
    @GetMapping("/statuts")
    public ResponseEntity<Commande.StatutCommande[]> getAllStatuts() {
        return ResponseEntity.ok(Commande.StatutCommande.values());
    }

    // ADD commande
    @PostMapping
    public ResponseEntity<Commande> addCommande(@RequestBody Commande commande) {
        // Set default status if not provided
        if (commande.getStatut() == null) {
            commande.setStatut(Commande.StatutCommande.PLANNIFIER);
        }

        Commande createdCommande = cService.addCommande(commande);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }

    // EDIT commande
    @PutMapping("/{id}")
    public ResponseEntity<?> editCommande(@PathVariable("id") Long id, @RequestBody Commande c) {
        try {
            c.setId(id);

            // Validate status if provided
            if (c.getStatut() != null) {
                // Status validation is handled by enum, but we can add additional checks here if needed
            }

            if (c.getCommandeProduits() != null) {
                for (CommandeProduit cp : c.getCommandeProduits()) {
                    Produit produit = cp.getProduit();
                    if (produit != null && produit.getId() != null) {
                        Produit existingProduit = produitService.getProduitById(produit.getId());
                        if (existingProduit != null) {
                            produit.setNomProduit(existingProduit.getNomProduit());
                            produit.setDescription(existingProduit.getDescription());
                            produit.setPrix(existingProduit.getPrix());
                            produit.setDate(existingProduit.getDate());
                        }
                    }
                    cp.setCommande(c);
                }
            }

            Commande updatedCommande = cService.editCommande(c);
            return ResponseEntity.ok(updatedCommande);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    // DELETE commande
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable("id") Long id) {
        cService.deleteCommandeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idCommande}/type-produits")
    public List<TypeProduit> getTypeProduitsParCommande(@PathVariable Long idCommande) {
        List<CommandeProduit> commandeProduits = commandeProduitRepository.findByCommandeId(idCommande);

        return commandeProduits.stream()
                .map(CommandeProduit::getProduit)
                .filter(Objects::nonNull)
                .map(Produit::getTypeProduit)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}