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

import java.util.ArrayList;
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

    @Override
    public Citerne addCiterne(Citerne citerneData) {
        if (citerneData.getCompartiments().size() != citerneData.getNombreCompartiments()) {
            throw new IllegalArgumentException("Le nombre de compartiments doit être exactement " + citerneData.getNombreCompartiments());
        }

        // Lier les compartiments à la citerne
        for (Compartiment c : citerneData.getCompartiments()) {
            if (c.getId() != null) {
                compartimentRepository.findById(c.getId())
                        .ifPresent(compartiment -> compartiment.setCiterne(citerneData));
            }
        }

        // Sauvegarder la citerne avec les compartiments liés
        return citerneRepository.save(citerneData);
    }

    @Override
    public Citerne updateCiterne(Long id, Citerne citerneData) {
        Citerne citerne = citerneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citerne non trouvée"));

        citerne.setReference(citerneData.getReference());
        citerne.setCapacite(citerneData.getCapacite());

        List<Compartiment> nouveauxCompartiments = new ArrayList<>();

        for (Compartiment c : citerneData.getCompartiments()) {
            Compartiment compartiment = compartimentRepository.findById(c.getId())
                    .orElseThrow(() -> new RuntimeException("Compartiment non trouvé"));
            compartiment.setCiterne(citerne); // très important
            nouveauxCompartiments.add(compartiment);
        }

        citerne.setCompartiments(nouveauxCompartiments);

        return citerneRepository.save(citerne);
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
