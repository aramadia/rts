package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.SpaceRtsGame;
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

      Vector2[] screenCoords = {new Vector2(0,0), new Vector2(SpaceRtsGame.SCENE_WIDTH, 0), new Vector2(SpaceRtsGame.SCENE_WIDTH, SpaceRtsGame.SCENE_HEIGHT), new Vector2(0, SpaceRtsGame.SCENE_HEIGHT)};
      // Collide with bottom wall
      if (movement.velocity.y < 0 && Intersector.intersectSegmentPolygon(screenCoords[0], screenCoords[1], bounds.newBounds)) {
        movement.velocity.y *= -1;
      }

      // Collide with right wall
      if (movement.velocity.x > 0 && Intersector.intersectSegmentPolygon(screenCoords[1], screenCoords[2], bounds.newBounds)) {
        movement.velocity.x *= -1;
      }

      // Collide with top wall
      if (movement.velocity.y > 0 && Intersector.intersectSegmentPolygon(screenCoords[2], screenCoords[3], bounds.newBounds)) {
        movement.velocity.y *= -1;
      }

      // Collide with left wall
      if (movement.velocity.x < 0 && Intersector.intersectSegmentPolygon(screenCoords[3], screenCoords[0], bounds.newBounds)) {
        movement.velocity.x *= -1;
      }
    }

  }

}
