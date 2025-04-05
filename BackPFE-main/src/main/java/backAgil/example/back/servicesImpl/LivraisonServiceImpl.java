package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Camion;
import backAgil.example.back.models.Livraison;
import backAgil.example.back.repositories.CamionRepository;
import backAgil.example.back.repositories.LivraisonRepository;
import backAgil.example.back.services.CamionService;
import backAgil.example.back.services.LivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class LivraisonServiceImpl implements LivraisonService {

    @Autowired
    private LivraisonRepository livraisonRepository;
    @Autowired
    private CamionRepository camionRepository;

    @Autowired
    private CamionService cService;

    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }

    public Optional<Livraison> getLivraisonById(Long id) {
        return livraisonRepository.findById(id);
    }

    @Override
    public Livraison addLivraison(Livraison livraison) {
        Camion camion = camionRepository.findById(livraison.getCamion().getId())
                .orElseThrow(() -> new RuntimeException("Camion introuvable"));

        livraison.setCamion(camion); // Associe le camion existant à la livraison
        return livraisonRepository.save(livraison);
    }


    public Livraison updateLivraison(Long id, Livraison updatedLivraison) {
        return livraisonRepository.findById(id).map(livraison -> {
            livraison.setDateLivraison(updatedLivraison.getDateLivraison());
            livraison.setStatut(updatedLivraison.getStatut());
            livraison.setCamion(updatedLivraison.getCamion());
            livraison.setCommandes(updatedLivraison.getCommandes());
            return livraisonRepository.save(livraison);
        }).orElseThrow(() -> new RuntimeException("Livraison non trouvée avec l'ID : " + id));
    }

    public void deleteLivraison(Long id) {
        livraisonRepository.deleteById(id);
    }
    public String getImmatriculationByMarque(String marque) {
        List<Camion> camions = cService.getCamionsByMarque(marque);
        if (!camions.isEmpty()) {
            return camions.get(0).getImmatriculation(); // Retourne l'immatriculation du premier camion trouvé
        }
        return "Camion non trouvé pour cette marque";
    }

}
