package backAgil.example.back.repositories;

import backAgil.example.back.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface LivraisonRepository extends JpaRepository<Livraison , Long> {
    boolean existsByCodeLivraison(String codeLivraison);
    List<Livraison> findByDateLivraison(Date dateLivraison);
    @Query("SELECT l FROM Livraison l JOIN l.commandes c WHERE c.user.userName = :username")
    List<Livraison> findByUserName(@Param("username") String username);


}
