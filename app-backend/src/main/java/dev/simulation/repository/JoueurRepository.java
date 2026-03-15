package dev.simulation.repository;

import dev.simulation.entity.Joueur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoueurRepository extends JpaRepository<Joueur, String> {

  Optional<Joueur> findByPseudo(String pseudo);
}
