package dev.simulation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "scores")
public class Score {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "joueur_id", nullable = false)
  private Joueur joueur;

  @Column(nullable = false)
  private long valeur;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Score() {
    // JPA
  }

  public Score(Joueur joueur, long valeur) {
    this.joueur = joueur;
    this.valeur = valeur;
  }

  public String getId() {
    return id;
  }

  public Joueur getJoueur() {
    return joueur;
  }

  public long getValeur() {
    return valeur;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
