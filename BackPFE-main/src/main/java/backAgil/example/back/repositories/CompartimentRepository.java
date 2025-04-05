package backAgil.example.back.repositories;

import backAgil.example.back.models.Compartiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompartimentRepository extends JpaRepository<Compartiment, Long> {
    List<Compartiment> findByCiterneId(Long citerneId);
}