package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class MovementSystem extends IteratingSystem {

  private final ComponentMapper<MovementComponent> movementMapper;
  private final ComponentMapper<TransformComponent> transformComponent;

  private Vector2 tmp = new Vector2();

  public MovementSystem() {
    super(Family.all(MovementComponent.class, TransformComponent.class).get());

    movementMapper = ComponentMapper.getFor(MovementComponent.class);
    transformComponent = ComponentMapper.getFor(TransformComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = movementMapper.get(entity);
    TransformComponent transform = this.transformComponent.get(entity);

    final int acceleration = 200;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      movement.acceleration.set(-acceleration, 0);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      movement.acceleration.set(acceleration,0);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      movement.acceleration.set(0, acceleration);
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      movement.acceleration.set(0, -acceleration);
    }

    // apply acceleration
    tmp.set(movement.acceleration).scl(deltaTime);
    movement.velocity.add(tmp);



    // apply velocity
    tmp.set(movement.velocity).scl(deltaTime);
    transform.position.add(tmp.x, tmp.y, 0);
  }

}
