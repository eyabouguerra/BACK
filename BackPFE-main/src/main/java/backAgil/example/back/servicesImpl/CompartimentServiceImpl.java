package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.repositories.CompartimentRepository;
import backAgil.example.back.services.CompartimentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompartimentServiceImpl implements CompartimentService {

    @Autowired
    private CompartimentRepository compartimentRepository;

    @Autowired
    private CiterneRepository citerneRepository;

    @Override
    public List<Compartiment> getAllCompartiments() {
        return compartimentRepository.findAll();
    }

    @Override
    public Optional<Compartiment> getCompartimentById(Long id) {
        return compartimentRepository.findById(id);
    }



    @Override
    public Compartiment addCompartiment(Compartiment compartiment) {
        return compartimentRepository.save(compartiment);  // Sauvegarde du compartiment dans la base
    }


    // Récupérer les compartiments d'une citerne
    public List<Compartiment> getCompartimentsByCiterneId(Long citerneId) {
        return compartimentRepository.findByCiterneId(citerneId);
    }



    @Override
    public Compartiment updateCompartiment(Long id, Compartiment newCompartiment) {
        return compartimentRepository.findById(id).map(compartiment -> {
            // Mettre à jour les champs du compartiment
            compartiment.setCapaciteMax(newCompartiment.getCapaciteMax());
            compartiment.setStatut(newCompartiment.getStatut());

            // Mettre à jour la référence si elle est présente
            if (newCompartiment.getReference() != null && !newCompartiment.getReference().isEmpty()) {
                compartiment.setReference(newCompartiment.getReference());
            }

            return compartimentRepository.save(compartiment);
        }).orElseThrow(() -> new IllegalArgumentException("Compartiment not found"));
    }

    @Override
    public void deleteCompartiment(Long id) {
        if (!compartimentRepository.existsById(id)) {
            throw new IllegalArgumentException("Compartiment not found");
        }
        compartimentRepository.deleteById(id);
    }

    @Transactional
    public Compartiment addCompartimentToCiterne(Long citerneId, Compartiment compartiment) {
        Citerne citerne = citerneRepository.findById(citerneId)
                .orElseThrow(() -> new IllegalArgumentException("Citerne not found"));

        // Si le compartiment n'est pas déjà persistant, l'attacher à la session
        if (compartiment.getId() == null || !compartimentRepository.existsById(compartiment.getId())) {
            // C'est un nouvel objet, il doit être persistant
            compartiment = compartimentRepository.save(compartiment);
        } else {
            // Si c'est un objet détaché, on le réassocie avant de l'enregistrer
            compartiment = compartimentRepository.saveAndFlush(compartiment);
        }

        // Maintenant, lier le compartiment à la citerne
        compartiment.setCiterne(citerne);

        // Sauvegarder de nouveau la citerne avec son compartiment associé
        return compartimentRepository.save(compartiment);  // Assurez-vous que tout est correctement attaché
    }






}
