package backAgil.example.back.repositories;

import backAgil.example.back.models.Commande;
import backAgil.example.back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    boolean existsByCodeCommande(String codeCommande);
    @Query("SELECT c FROM Commande c " +
            "LEFT JOIN FETCH c.commandeProduits cp " +
            "LEFT JOIN FETCH cp.produit p " +
            "LEFT JOIN FETCH p.typeProduit " +
            "WHERE c.id = :id")
    Optional<Commande> findCommandeWithProduitsAndTypes(@Param("id") Long id);
    void deleteById(Long id);
    List<Commande> findByUser(User user);


    List<Commande> findByStatut(Commande.StatutCommande statut);
}
