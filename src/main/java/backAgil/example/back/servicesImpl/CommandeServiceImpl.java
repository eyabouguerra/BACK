package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Client;
import backAgil.example.back.models.Commande;
import backAgil.example.back.models.CommandeProduit;
import backAgil.example.back.models.Produit;
import backAgil.example.back.repositories.ClientRepository;
import backAgil.example.back.repositories.CommandeRepository;
import backAgil.example.back.repositories.ProduitRepository;
import backAgil.example.back.repositories.commandeProduitRepository;
import backAgil.example.back.services.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Commande addCommande(Commande commande) {
        if (commande.getCommandeProduits() == null) {
            commande.setCommandeProduits(new ArrayList<>());
        }

        // Set default status if not provided
        if (commande.getStatut() == null) {
            commande.setStatut(Commande.StatutCommande.PLANNIFIER);
        }

        // Handle client creation or association
        if (commande.getClient() != null) {
            Client client = commande.getClient();
            if (client.getClientId() == null && client.getFullName() != null) {
                // Create a new client
                Client newClient = new Client();
                newClient.setFullName(client.getFullName());
                newClient.setFullAddress(client.getFullAddress());
                newClient.setContactNumber(client.getContactNumber());
                newClient.setAlternateContactNumber(client.getAlternateContactNumber());
                newClient.setLatitude(client.getLatitude());
                newClient.setLongitude(client.getLongitude());

                client = clientRepository.save(newClient);
                commande.setClient(client);
            }
            else if (client.getClientId() != null) {
                // Retrieve existing client
                Client existingClient = clientRepository.findById(client.getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));

                // Update client details if provided in the JSON
                if (client.getFullName() != null && !client.getFullName().equals(existingClient.getFullName())) {
                    existingClient.setFullName(client.getFullName());
                }
                if (client.getFullAddress() != null && !client.getFullAddress().equals(existingClient.getFullAddress())) {
                    existingClient.setFullAddress(client.getFullAddress());
                }
                if (client.getContactNumber() != null && !client.getContactNumber().equals(existingClient.getContactNumber())) {
                    existingClient.setContactNumber(client.getContactNumber());
                }
                if (client.getAlternateContactNumber() != null && !client.getAlternateContactNumber().equals(existingClient.getAlternateContactNumber())) {
                    existingClient.setAlternateContactNumber(client.getAlternateContactNumber());
                }
                if (client.getLatitude() != null && !client.getLatitude().equals(existingClient.getLatitude())) {
                    existingClient.setLatitude(client.getLatitude());
                }
                if (client.getLongitude() != null && !client.getLongitude().equals(existingClient.getLongitude())) {
                    existingClient.setLongitude(client.getLongitude());
                }

                // Save the updated client
                client = clientRepository.save(existingClient);
                commande.setClient(client);
            } else {
                throw new IllegalArgumentException("Les détails du client sont incomplets");
            }
        }

        List<CommandeProduit> commandeProduits = new ArrayList<>();
        Float totalPrice = 0.0f;

        for (CommandeProduit cp : commande.getCommandeProduits()) {
            Produit produit = pRepo.findById(cp.getProduit().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

            cp.setProduit(produit);
            if (cp.getQuantite() == null || cp.getQuantite() <= 0) {
                cp.setQuantite(1.0f);
            }

            Float prixProduit = produit.getPrix();
            float sousTotal = cp.getQuantite() * prixProduit;
            totalPrice += sousTotal;

            cp.setCommande(commande);
            commandeProduits.add(cp);
        }

        commande.setTotalPrice(totalPrice);

        Commande savedCommande = cRepo.save(commande);
        commandeProduitRepository.saveAll(commandeProduits);

        return savedCommande;
    }

    @Override
    public Commande editCommande(Commande updatedCommande) {
        Commande existingCommande = cRepo.findById(updatedCommande.getId())
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        existingCommande.setCodeCommande(updatedCommande.getCodeCommande());

        existingCommande.setDateCommande(updatedCommande.getDateCommande());
        existingCommande.setPrice(updatedCommande.getPrice());
        existingCommande.setTotalPrice(updatedCommande.getTotalPrice());

        // Update status if provided
        if (updatedCommande.getStatut() != null) {
            existingCommande.setStatut(updatedCommande.getStatut());
        }

        // Update client if provided
        if (updatedCommande.getClient() != null && updatedCommande.getClient().getClientId() != null) {
            Client client = clientRepository.findById(updatedCommande.getClient().getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));
            existingCommande.setClient(client);
        } else if (updatedCommande.getClient() == null) {
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

    @Override
    public boolean checkCodeCommandeExists(String codeCommande) {
        return cRepo.existsByCodeCommande(codeCommande);
    }
}