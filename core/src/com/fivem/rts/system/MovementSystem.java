package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.component.BoundsComponent;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class MovementSystem extends IteratingSystem {

  private static final int ACCURACY = 5;

  private final ComponentMapper<MovementComponent> movementMapper;
  private final ComponentMapper<TransformComponent> transformMapper;
  private final ComponentMapper<DestinationComponent> destinationMapper;
  private final ComponentMapper<BoundsComponent> boundsMapper;

  private Vector2 tmp = new Vector2();

  public MovementSystem() {
    super(Family.all(MovementComponent.class, TransformComponent.class, BoundsComponent.class).get());
    movementMapper = ComponentMapper.getFor(MovementComponent.class);
    transformMapper = ComponentMapper.getFor(TransformComponent.class);
    destinationMapper = ComponentMapper.getFor(DestinationComponent.class);
    boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = movementMapper.get(entity);
    TransformComponent transform = transformMapper.get(entity);
    DestinationComponent destination = destinationMapper.get(entity);
    BoundsComponent bounds = boundsMapper.get(entity);

    if (destination != null) {
      // dumb movement at static speed towards target
      if (destination.position.x < transform.position.x) {
        movement.acceleration.x = -100;
      } else {
        movement.acceleration.x = 100;
      }

      if (destination.position.y < transform.position.y) {
        movement.acceleration.y = -100;
      } else {
        movement.acceleration.y = 100;
      }


      float x = Math.abs(transform.position.x - destination.position.x);
      float y = Math.abs(transform.position.y - destination.position.y);
      if (x < ACCURACY) {
        movement.acceleration.x = 0;
        movement.velocity.x = 0;
      }

      if (y < ACCURACY) {
        movement.acceleration.y = 0;
        movement.velocity.y = 0;
      }

      if (x < ACCURACY && y < ACCURACY) {
        // close enough to the target, stop moving
        entity.remove(DestinationComponent.class);
      }
    }

    // apply acceleration
    tmp.set(movement.acceleration).scl(deltaTime);
    movement.velocity.add(tmp);

    // apply velocity
    tmp.set(movement.velocity).scl(deltaTime);
    transform.position.add(tmp.x, tmp.y, 0);

    // Set orientation of object to point in the same direction of velocity
    // TODO: Add a rotation offset to each sprite?
    transform.rotation = movement.velocity.angle() - 90;

    //update bounds
    bounds.polygon.translate(tmp.x, tmp.y);
    bounds.polygon.setRotation(transform.rotation);
    bounds.polygon.setScale(transform.scale.x, transform.scale.y);
  }

}
