package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.fivem.rts.SpaceRts;
import com.fivem.rts.component.BoundsComponent;
import com.fivem.rts.component.MovementComponent;

public class CollisionSystem extends EntitySystem {

  private final ComponentMapper<BoundsComponent> boundsMapper;
  private final ComponentMapper<MovementComponent> movementMapper;

  private ImmutableArray<Entity> entities;

  public CollisionSystem() {
    super();

    boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
    movementMapper = ComponentMapper.getFor(MovementComponent.class);
  }

  @Override
  public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(Family.all(BoundsComponent.class, MovementComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    for (int i = 0; i < entities.size(); ++i) {
      Entity entity = entities.get(i);
      BoundsComponent bounds = boundsMapper.get(entity);
      MovementComponent movement = movementMapper.get(entity);

      // Do classic bounce with walls
      if (bounds.bounds.x < 0 && movement.velocity.x < 0) {
        movement.velocity.x *= -1;
      }
      if (bounds.bounds.x + bounds.bounds.width > SpaceRts.SCENE_WIDTH && movement.velocity.x > 0) {
        movement.velocity.x *= -1;
      }

      if (bounds.bounds.y < 0 && movement.velocity.y < 0) {
        movement.velocity.y *= -1;
      }
      if (bounds.bounds.y + bounds.bounds.height > SpaceRts.SCENE_HEIGHT && movement.velocity.y > 0) {
        movement.velocity.y *= -1;
      }

      // Collide with other entities
      // For now just reverse velocity
      for (int j = i+1; j < entities.size(); ++j) {
        Entity otherEntity = entities.get(j);
        BoundsComponent otherEntityBounds = boundsMapper.get(otherEntity);
        MovementComponent otherEntityMovement = movementMapper.get(otherEntity);

        // TODO there has got to be a better way to do it than this shit
        if (bounds.bounds.overlaps(otherEntityBounds.bounds)) {
          float xOverlap = 0;
          float yOverlap = 0;

          if (bounds.bounds.x + bounds.bounds.width >= otherEntityBounds.bounds.x ||
              otherEntityBounds.bounds.x + otherEntityBounds.bounds.width >= bounds.bounds.x) {
            float x = bounds.bounds.x + bounds.bounds.width - otherEntityBounds.bounds.x;
            float x2 = otherEntityBounds.bounds.x + otherEntityBounds.bounds.width - bounds.bounds.x;
            xOverlap = Math.max(x, x2);
          }

          if (bounds.bounds.y + bounds.bounds.height >= otherEntityBounds.bounds.y ||
              otherEntityBounds.bounds.y + otherEntityBounds.bounds.height >= bounds.bounds.y) {
            float y = bounds.bounds.y + bounds.bounds.height - otherEntityBounds.bounds.y;
            float y2 = otherEntityBounds.bounds.y + otherEntityBounds.bounds.height - bounds.bounds.y;
            yOverlap = Math.max(y, y2);
          }

          if (xOverlap > yOverlap) {
            movement.velocity.x = Math.abs(movement.velocity.x);
            otherEntityMovement.velocity.x = Math.abs(otherEntityMovement.velocity.x);

            if (bounds.bounds.x < otherEntityBounds.bounds.x) {
              movement.velocity.x *= -1;
            } else {
              otherEntityMovement.velocity.x *= -1;
            }
          } else {
            movement.velocity.y = Math.abs(movement.velocity.y);
            otherEntityMovement.velocity.y = Math.abs(otherEntityMovement.velocity.y);

            if (bounds.bounds.y < otherEntityBounds.bounds.y) {
              movement.velocity.y *= -1;
            } else {
              otherEntityMovement.velocity.y *= -1;
            }
          }
        }
      }
    }

  }

}
