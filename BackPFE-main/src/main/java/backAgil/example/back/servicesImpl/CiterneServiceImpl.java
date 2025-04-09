package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Citerne;
import backAgil.example.back.models.Compartiment;
import backAgil.example.back.repositories.CiterneRepository;
import backAgil.example.back.repositories.CompartimentRepository;
import backAgil.example.back.services.CiterneService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CiterneServiceImpl implements CiterneService {

    @Autowired
    private CiterneRepository citerneRepository;
    @Autowired
    private CompartimentRepository compartimentRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Citerne> getAllCiternes() {
        return citerneRepository.findAll();
    }

    @Override
    public Optional<Citerne> getCiterneById(Long id) {
        return citerneRepository.findById(id);
    }


    @Transactional
    public Citerne addCiterne(Citerne citerne) {
        // Ensure only existing Compartiments are linked
        List<Compartiment> compartiments = compartimentRepository.findAllById(
                citerne.getCompartiments().stream()
                        .map(Compartiment::getId)
                        .collect(Collectors.toList())
        );

        // Check if any compartiment is not found
        if (compartiments.size() != citerne.getCompartiments().size()) {
            throw new IllegalArgumentException("Certain compartiments were not found.");
        }

        citerne.setCompartiments(compartiments);
        return citerneRepository.save(citerne);
    }



    @Override
    public Citerne updateCiterne(Long id, Citerne newCiterne) {
        return citerneRepository.findById(id).map(citerne -> {
            citerne.setReference(newCiterne.getReference());
            citerne.setCapacite(newCiterne.getCapacite());

            if (newCiterne.getCompartiments() != null) {
                // Dissocier anciens compartiments
                for (Compartiment c : citerne.getCompartiments()) {
                    c.setCiterne(null);
                    compartimentRepository.save(c);
                }

                // Associer les nouveaux compartiments
                List<Compartiment> updatedCompartiments = newCiterne.getCompartiments().stream()
                        .map(c -> {
                            Compartiment existing = compartimentRepository.findById(c.getId())
                                    .orElseThrow(() -> new EntityNotFoundException("Compartiment non trouvé"));
                            existing.setCiterne(citerne);
                            return compartimentRepository.save(existing);
                        })
                        .collect(Collectors.toList());

                citerne.setCompartiments(updatedCompartiments);
            }

            return citerneRepository.save(citerne);
        }).orElseThrow(() -> new EntityNotFoundException("Citerne non trouvée avec l'id: " + id));
    }



    @Override
    public void deleteCiterne(Long id) {
        if (!citerneRepository.existsById(id)) {
            throw new EntityNotFoundException("Citerne non trouvée avec l'id: " + id);
        }
        citerneRepository.deleteById(id);
    }

    @Transactional
    public Compartiment addCompartimentToCiterne(Long citerneId, Compartiment compartiment) {
        Citerne citerne = citerneRepository.findById(citerneId)
                .orElseThrow(() -> new IllegalArgumentException("Citerne not found"));

        compartiment.setCiterne(citerne);
        return compartimentRepository.save(compartiment);
    }


}
