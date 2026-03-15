package dev.simulation.repository;

import dev.simulation.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, String> {}
