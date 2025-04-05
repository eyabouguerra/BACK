package backAgil.example.back.controllers;

import backAgil.example.back.models.Commande;
import backAgil.example.back.models.Produit;
import backAgil.example.back.services.CommandeService;
import backAgil.example.back.services.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/commandes/v1")
@CrossOrigin("*")
@RestController
public class CommandeController {

    @Autowired
    private CommandeService cService;

    @Autowired
    private ProduitService produitService;

    // METH 1 : GET All Commandes
    @GetMapping
    public List<Commande> getAll() {
        List<Commande> commandes = cService.getAllCommandes();
        commandes.forEach(c -> System.out.println("Commande ID: " + c.getId() + ", Price: " + c.getPrice()));
        return commandes;
    }

    // METH 2 : GET Commande By ID
    @GetMapping("/{id}")
    public Commande getCommandeById(@PathVariable("id") Long id) {
        return cService.getCommandeById(id);
    }

    // METH 3 : ADD Commande
    @PostMapping
    public ResponseEntity<?> addCommande(@RequestBody Commande commande) {
        try {
            Commande newCommande = cService.addCommande(commande);
            return ResponseEntity.ok(newCommande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    // METH 4 : EDIT Commande
    @PutMapping("/{id}")
    public ResponseEntity<?> editCommande(@PathVariable("id") Long id, @RequestBody Commande c) {
        try {
            // Assigne l'ID de l'URL à l'objet Commande
            c.setId(id);

            // Si les produits sont modifiés, on met à jour les produits uniquement dans cette commande
            if (c.getProduits() != null) {
                for (Produit produit : c.getProduits()) {
                    // Vérifie si le produit est dans la base de données
                    Produit existingProduit = produitService.getProduitById(produit.getId());
                    if (existingProduit != null) {
                        // Si le produit existe déjà, on met à jour uniquement les champs pertinents
                        // Cela ne modifiera pas les autres produits dans la base de données
                        // MAIS ici, on met à jour uniquement les produits dans cette commande
                        existingProduit.setNomProduit(produit.getNomProduit());
                        existingProduit.setDescription(produit.getDescription());
                        existingProduit.setPrix(produit.getPrix());
                        existingProduit.setDate(produit.getDate());

                        // On ne met à jour ce produit que dans cette commande, pas dans la base de données globale
                        // Ne pas appeler produitService.editProduit ici
                        produit.setNomProduit(existingProduit.getNomProduit());
                        produit.setDescription(existingProduit.getDescription());
                        produit.setPrix(existingProduit.getPrix());
                        produit.setDate(existingProduit.getDate());
                    }
                }
            }

            // Mise à jour de la commande dans la base de données
            Commande updatedCommande = cService.editCommande(c);
            return ResponseEntity.ok(updatedCommande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    // METH 5 : DELETE Commande
    @DeleteMapping("/{id}")
    public void deleteCommande(@PathVariable("id") Long id) {
        cService.deleteCommandeById(id);
    }
}
