package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.SpaceRts;
import com.fivem.rts.component.BoundsComponent;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class MovementSystem extends IteratingSystem {

  private final ComponentMapper<MovementComponent> movementMapper;
  private final ComponentMapper<TransformComponent> transformMapper;
  private final ComponentMapper<BoundsComponent> boundsMapper;

  private Vector2 tmp = new Vector2();

  public MovementSystem() {
    super(Family.all(MovementComponent.class, TransformComponent.class, BoundsComponent.class).get());

    movementMapper = ComponentMapper.getFor(MovementComponent.class);
    transformMapper = ComponentMapper.getFor(TransformComponent.class);
    boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = movementMapper.get(entity);
    TransformComponent transform = transformMapper.get(entity);
    BoundsComponent bounds = boundsMapper.get(entity);

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

    // Do classic bounce with walls
    if (transform.position.x < 0 && movement.velocity.x < 0) {
      movement.velocity.x *= -1;
    }
    if (transform.position.x + bounds.bounds.width > SpaceRts.SCENE_WIDTH && movement.velocity.x > 0) {
      movement.velocity.x *= -1;
    }

    if (transform.position.y < 0 && movement.velocity.y < 0) {
      movement.velocity.y *= -1;
    }
    if (transform.position.y + bounds.bounds.height > SpaceRts.SCENE_HEIGHT && movement.velocity.y > 0) {
      movement.velocity.y *= -1;
    }

    // apply acceleration
    tmp.set(movement.acceleration).scl(deltaTime);
    movement.velocity.add(tmp);

    // apply velocity
    tmp.set(movement.velocity).scl(deltaTime);
    transform.position.add(tmp.x, tmp.y, 0);
  }

}
