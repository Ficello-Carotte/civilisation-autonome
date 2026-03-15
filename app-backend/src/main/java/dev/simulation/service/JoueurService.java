package dev.simulation.service;

import dev.simulation.entity.Joueur;
import dev.simulation.repository.JoueurRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JoueurService {

  private final JoueurRepository joueurRepository;

  public JoueurService(JoueurRepository joueurRepository) {
    this.joueurRepository = joueurRepository;
  }

  @Transactional(readOnly = true)
  public Optional<Joueur> findByPseudo(String pseudo) {
    return joueurRepository.findByPseudo(pseudo);
  }

  @Transactional
  public Joueur save(Joueur joueur) {
    return joueurRepository.save(joueur);
  }
}
