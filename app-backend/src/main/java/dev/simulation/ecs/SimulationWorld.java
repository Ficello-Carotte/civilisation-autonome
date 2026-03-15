package dev.simulation.ecs;

/**
 * Monde de simulation (Dominion ECS). Point d'entrée pour les entités et systèmes de la simulation.
 *
 * <p>À brancher sur l'API Dominion ECS (Engine / Composition) quand la dépendance sera utilisée
 * côté jeu. Pour l'instant placeholder pour que le projet compile.
 */
public class SimulationWorld {

  public SimulationWorld() {}

  /** Réservé pour l'accès à l'engine Dominion ECS (ex. Engine.builder().build()). */
  public Object getEngine() {
    return null;
  }

  /** Réservé pour le scheduler ECS. */
  public Object getScheduler() {
    return null;
  }
}
