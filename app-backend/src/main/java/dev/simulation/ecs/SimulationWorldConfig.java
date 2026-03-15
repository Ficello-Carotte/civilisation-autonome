package dev.simulation.ecs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulationWorldConfig {

  @Bean
  public SimulationWorld simulationWorld() {
    return new SimulationWorld();
  }
}
