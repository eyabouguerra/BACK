package backAgil.example.back.repositories;

import backAgil.example.back.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivraisonRepository extends JpaRepository<Livraison , Long> {
}
