package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.PlayerComponent;
import com.fivem.rts.component.TransformComponent;
import com.fivem.rts.component.ZombieComponent;

public class AiSystem extends EntitySystem {

  private Engine engine;

  public AiSystem() {
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;

  }

  @Override
  public void update(float deltaTime) {
    ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    ImmutableArray<Entity> zombies = engine.getEntitiesFor(Family.all(ZombieComponent.class).get());

    // TODO stop assuming 1 player
    Entity player = players.get(0);

    for (Entity zombie : zombies) {
      // zombies swarm players
      DestinationComponent destination = zombie.getComponent(DestinationComponent.class);
      if (destination == null) {
        destination = new DestinationComponent();
        zombie.add(destination);
      }

      TransformComponent playerTransform = player.getComponent(TransformComponent.class);
      destination.position.set(playerTransform.position.x, playerTransform.position.y);
    }

  }
}
