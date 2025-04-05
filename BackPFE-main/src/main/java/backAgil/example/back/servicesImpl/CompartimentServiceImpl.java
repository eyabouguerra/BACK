package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.repositories.CompartimentRepository;
import backAgil.example.back.services.CompartimentService;
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
    public List<Compartiment> getCompartimentsByCiterneId(Long citerneId) {
        Citerne citerne = citerneRepository.findById(citerneId)
                .orElseThrow(() -> new IllegalArgumentException("Citerne not found"));
        List<Compartiment> compartiments = compartimentRepository.findByCiterneId(citerneId);

        double totalCompartimentCapacity = compartiments.stream().mapToDouble(Compartiment::getCapaciteMax).sum();

        if (totalCompartimentCapacity > citerne.getCapacite()) {
            throw new IllegalArgumentException("Total compartments capacity exceeds citerne capacity");
        }

        return compartiments;
    }



    @Override
    public Compartiment addCompartiment(Compartiment compartiment) {
        if (compartiment.getCiterne() != null && compartiment.getCiterne().getId() != null) {
            Citerne citerne = citerneRepository.findById(compartiment.getCiterne().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Citerne does not exist"));
            compartiment.setCiterne(citerne);
        } else {
            compartiment.setCiterne(null);  // Si aucune citerne n'est envoyée, ne pas l'ajouter
        }

        // Vérification de la référence
        if (compartiment.getReference() == null || compartiment.getReference().isEmpty()) {
            throw new IllegalArgumentException("Reference must be provided");
        }

        return compartimentRepository.save(compartiment);
    }


    @Override
    public Compartiment updateCompartiment(Long id, Compartiment newCompartiment) {
        return compartimentRepository.findById(id).map(compartiment -> {
            // Valider si le Citerne existe toujours
            if (newCompartiment.getCiterne() == null || newCompartiment.getCiterne().getId() == null) {
                throw new IllegalArgumentException("Citerne must be provided for the compartiment");
            }

            // Vérifier l'existence du Citerne dans la base de données
            Citerne citerne = citerneRepository.findById(newCompartiment.getCiterne().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Citerne does not exist"));

            // Mettre à jour les champs du compartiment
            compartiment.setCapaciteMax(newCompartiment.getCapaciteMax());
            compartiment.setStatut(newCompartiment.getStatut());
            compartiment.setCiterne(citerne);

            // Mettre à jour la référence si elle est présente
            if (newCompartiment.getReference() != null && !newCompartiment.getReference().isEmpty()) {
                compartiment.setReference(newCompartiment.getReference());
            }

            // Enregistrer et retourner le compartiment mis à jour
            return compartimentRepository.save(compartiment);
        }).orElse(null);
    }



    @Override
    public void deleteCompartiment(Long id) {
        if (!compartimentRepository.existsById(id)) {
            throw new IllegalArgumentException("Compartiment not found");
        }
        compartimentRepository.deleteById(id);
    }
}