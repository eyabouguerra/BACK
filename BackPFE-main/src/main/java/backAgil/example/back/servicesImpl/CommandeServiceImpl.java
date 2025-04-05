package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Commande;
import backAgil.example.back.models.Produit;
import backAgil.example.back.repositories.CommandeRepository;
import backAgil.example.back.repositories.ProduitRepository;
import backAgil.example.back.services.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeServiceImpl implements CommandeService {

    @Autowired
    private CommandeRepository cRepo;

    @Autowired
    private ProduitRepository pRepo;

    @Override
    public List<Commande> getAllCommandes() {
        return cRepo.findAll();
    }

    @Override
    public Commande getCommandeById(Long id) {
        return cRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteCommandeById(Long id) {
        cRepo.deleteById(id);
    }

    @Override
    public Commande addCommande(Commande c) {


        return cRepo.save(c);
    }


    @Override
    public Commande editCommande(Commande c) {
        if (c.getId() == null || !cRepo.existsById(c.getId())) {
            throw new IllegalArgumentException("La commande à modifier n'existe pas.");
        }
        return cRepo.save(c);
    }
}
