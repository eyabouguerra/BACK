package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.*;
import backAgil.example.back.repositories.*;
import backAgil.example.back.services.CommandeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandeServiceImpl implements CommandeService {

    @Autowired
    private CommandeRepository cRepo;

    @Autowired
    private commandeProduitRepository commandeProduitRepository;

    @Autowired
    private ProduitRepository pRepo;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = cRepo.findAll();
        commandes.forEach(commande -> {
            if (commande.getClient() != null) {
                commande.getClient().getFullName(); // Force client loading
            }
            commande.getCommandeProduits().forEach(cp -> {
                Produit produit = cp.getProduit();
                if (produit != null) {
                    produit.getTypeProduit(); // Force typeProduit loading
                }
            });
        });
        return commandes;
    }


    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(username).orElse(null);
    }

    @Override
    public List<Commande> getCommandesByCurrentUser() {
        User currentUser = getCurrentUser();
        return (currentUser != null) ? cRepo.findByUser(currentUser) : List.of();
    }


    @Override
    public Commande getCommandeById(Long id) {
        Commande commande = cRepo.findById(id).orElse(null);
        if (commande != null) {
            // Initialisation explicite des relations
            commande.getCommandeProduits().forEach(cp -> {
                Produit produit = cp.getProduit();
                if (produit != null) {
                    produit.getTypeProduit(); // force le chargement du type de produit
                }
            });
            // Initialiser le client si nécessaire
            if (commande.getClient() != null) {
                commande.getClient().getFullName(); // force le chargement du client
            }
        }
        return commande;
    }

    @Override
    public void deleteCommandeById(Long id) {
        cRepo.deleteById(id);
    }

    @Transactional
    @Override
    public Commande addCommande(Commande commande) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Utilisateur non connecté");
        }
        commande.setUser(currentUser);
        // Set default status to EN_COURS
        commande.setStatut(Commande.StatutCommande.EN_COURS);
        if (commande.getCommandeProduits() == null) {
            commande.setCommandeProduits(new ArrayList<>());
        }

        // Traiter le client
        if (commande.getClient() != null) {
            Client client = commande.getClient();
            if (client.getClientId() == null) {
                client = clientRepository.save(client);
                commande.setClient(client);
            } else {
                client = clientRepository.findById(client.getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));
                commande.setClient(client);
            }
        }

        // Sauvegarder la commande sans les produits d'abord
        Commande savedCommande = cRepo.save(commande);

        // Traiter les produits de la commande
        List<CommandeProduit> commandeProduits = new ArrayList<>();
        Float totalPrice = 0f;

        for (CommandeProduit cp : commande.getCommandeProduits()) {
            Produit produit = pRepo.findById(cp.getProduit().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
            cp.setProduit(produit);
            if (cp.getQuantite() == null || cp.getQuantite() <= 0) {
                cp.setQuantite(1f);
            }
            cp.setCommande(savedCommande);
            commandeProduits.add(cp);
            totalPrice += cp.getQuantite() * produit.getPrix();
        }

        commandeProduitRepository.saveAll(commandeProduits);

        // Mettre à jour le prix total et sauvegarder à nouveau
        savedCommande.setTotalPrice(totalPrice);
        savedCommande.setCommandeProduits(commandeProduits);
        return cRepo.save(savedCommande);
    }

    @Override
    public List<Commande> getCommandesByStatut(Commande.StatutCommande statut) {
        List<Commande> commandes = cRepo.findByStatut(statut);
        commandes.forEach(commande -> {
            if (commande.getClient() != null) {
                commande.getClient().getFullName(); // Force client loading
            }
            commande.getCommandeProduits().forEach(cp -> {
                Produit produit = cp.getProduit();
                if (produit != null) {
                    produit.getTypeProduit(); // Force typeProduit loading
                }
            });
        });
        return commandes;
    }

    @Override
    public Commande updateStatutCommande(Long id, Commande.StatutCommande nouveauStatut) {
        Commande commande = cRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        commande.setStatut(nouveauStatut);
        return cRepo.save(commande);
    }


    @Override
    public Commande editCommande(Commande updatedCommande) {
        Commande existingCommande = cRepo.findById(updatedCommande.getId())
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        existingCommande.setCodeCommande(updatedCommande.getCodeCommande());
        existingCommande.setDateCommande(updatedCommande.getDateCommande());
        existingCommande.setPrice(updatedCommande.getPrice());
        existingCommande.setTotalPrice(updatedCommande.getTotalPrice());

        // Update client if provided
        if (updatedCommande.getClient() != null && updatedCommande.getClient().getClientId() != null) {
            Client client = clientRepository.findById(updatedCommande.getClient().getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));
            existingCommande.setClient(client);
        } else {
            existingCommande.setClient(null); // Allow clearing the client if needed
        }

        // Supprimer les anciennes relations et ajouter les nouvelles
        existingCommande.getCommandeProduits().clear();
        if (updatedCommande.getCommandeProduits() != null) {
            for (CommandeProduit cp : updatedCommande.getCommandeProduits()) {
                Produit produit = pRepo.findById(cp.getProduit().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
                cp.setProduit(produit);
                cp.setCommande(existingCommande); // rattache la commande
                existingCommande.getCommandeProduits().add(cp);
            }
        }

        return cRepo.save(existingCommande);
    }

    @Transactional
    public Commande syncWithLivraison(Long commandeId, Commande.StatutCommande livraisonStatut) {
        Commande commande = cRepo.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
        if (livraisonStatut == Commande.StatutCommande.LIVRE) {
            commande.setStatut(Commande.StatutCommande.LIVRE);
        }
        return cRepo.save(commande);
    }
    

    @Override
    public boolean checkCodeCommandeExists(String codeCommande) {
        return cRepo.existsByCodeCommande(codeCommande);
    }
}