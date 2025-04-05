package backAgil.example.back.repositories;

import backAgil.example.back.models.Commande;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
