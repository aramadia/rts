package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class MovementSystem extends IteratingSystem {

  private final ComponentMapper<MovementComponent> movementMapper;
  private final ComponentMapper<TransformComponent> transformMapper;

  private Vector2 tmp = new Vector2();

  public MovementSystem() {
    super(Family.all(MovementComponent.class, TransformComponent.class).get());
    movementMapper = ComponentMapper.getFor(MovementComponent.class);
    transformMapper = ComponentMapper.getFor(TransformComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = movementMapper.get(entity);
    TransformComponent transform = transformMapper.get(entity);

    // apply acceleration
    tmp.set(movement.acceleration).scl(deltaTime);
    movement.velocity.add(tmp);

    // apply velocity
    tmp.set(movement.velocity).scl(deltaTime);
    transform.position.add(tmp.x, tmp.y, 0);
  }

}
