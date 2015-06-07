package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class MovementSystem extends IteratingSystem {

  private final ComponentMapper<MovementComponent> movementMapper;
  private final ComponentMapper<TransformComponent> transformComponent;

  public MovementSystem() {
    super(Family.all(MovementComponent.class, TransformComponent.class).get());

    movementMapper = ComponentMapper.getFor(MovementComponent.class);
    transformComponent = ComponentMapper.getFor(TransformComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movementComponent = movementMapper.get(entity);
    TransformComponent transformComponent = this.transformComponent.get(entity);

    transformComponent.position.x += movementComponent.velocity.x;
    transformComponent.position.y += movementComponent.velocity.y;
  }

}
