package backAgil.example.back.repositories;

import backAgil.example.back.models.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByTypeProduit_Id(Long typeId);
}
