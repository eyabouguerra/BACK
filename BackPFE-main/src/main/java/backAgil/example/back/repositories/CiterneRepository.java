package backAgil.example.back.repositories;

import backAgil.example.back.models.Citerne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiterneRepository extends JpaRepository<Citerne, Long> {
}