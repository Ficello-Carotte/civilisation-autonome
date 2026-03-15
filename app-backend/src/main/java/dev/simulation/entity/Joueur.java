package dev.simulation.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "joueurs")
public class Joueur {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String pseudo;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "joueur", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Score> scores = new ArrayList<>();

  protected Joueur() {
    // JPA
  }

  public Joueur(String pseudo) {
    this.pseudo = pseudo;
  }

  public String getId() {
    return id;
  }

  public String getPseudo() {
    return pseudo;
  }

  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<Score> getScores() {
    return scores;
  }
}
