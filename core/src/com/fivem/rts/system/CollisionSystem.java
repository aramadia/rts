package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.component.*;

public class CollisionSystem extends EntitySystem {

  private static final String TAG = CollisionSystem.class.getSimpleName();

  private final ComponentMapper<BoundsComponent> boundsMapper;
  private final ComponentMapper<MovementComponent> movementMapper;

  private ImmutableArray<Entity> entities;
  private Engine engine;

  public CollisionSystem() {
    super();

    boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
    movementMapper = ComponentMapper.getFor(MovementComponent.class);
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
    entities = engine.getEntitiesFor(Family.all(BoundsComponent.class, MovementComponent.class, TransformComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    for (int i = 0; i < entities.size(); ++i) {
      Entity entity = entities.get(i);
      BoundsComponent bounds = boundsMapper.get(entity);
      MovementComponent movement = movementMapper.get(entity);

      Vector2[] screenCoords = {new Vector2(0,0), new Vector2(SpaceRtsGame.SCENE_WIDTH, 0), new Vector2(SpaceRtsGame.SCENE_WIDTH, SpaceRtsGame.SCENE_HEIGHT), new Vector2(0, SpaceRtsGame.SCENE_HEIGHT)};
      // Collide with bottom wall
      if (movement.velocity.y < 0 && Intersector.intersectSegmentPolygon(screenCoords[0], screenCoords[1], bounds.polygon)) {
        movement.velocity.y *= -1;
      }

      // Collide with right wall
      if (movement.velocity.x > 0 && Intersector.intersectSegmentPolygon(screenCoords[1], screenCoords[2], bounds.polygon)) {
        movement.velocity.x *= -1;
      }

      // Collide with top wall
      if (movement.velocity.y > 0 && Intersector.intersectSegmentPolygon(screenCoords[2], screenCoords[3], bounds.polygon)) {
        movement.velocity.y *= -1;
      }

      // Collide with left wall
      if (movement.velocity.x < 0 && Intersector.intersectSegmentPolygon(screenCoords[3], screenCoords[0], bounds.polygon)) {
        movement.velocity.x *= -1;
      }
    }

    Entity entity;
    Entity entity2;
    BoundsComponent bounds;
    BoundsComponent bounds2;
    for (int i = 0; i < entities.size(); i++) {
      entity = entities.get(i);
      for (int j = i+1; j < entities.size(); j++) {
        entity2 = entities.get(j);
        bounds = entity.getComponent(BoundsComponent.class);
        bounds2 = entity2.getComponent(BoundsComponent.class);
        if (bounds.polygon.getBoundingRectangle().overlaps(bounds2.polygon.getBoundingRectangle())
            && Intersector.overlapConvexPolygons(bounds.polygon, bounds2.polygon)) {
          if (entity.getComponent(BulletComponent.class) != null) {
            if (entity2.getComponent(ZombieComponent.class) != null) {
              // kill zombie
              Gdx.app.log(TAG, "Killed zombie " + entity2.getComponent(TextComponent.class).text);
              engine.removeEntity(entity2);
              engine.removeEntity(entity);

              engine.addEntity(createBloodEntity(entity2));
            }
          } else if (entity.getComponent(ZombieComponent.class) != null) {
            if (entity2.getComponent(BulletComponent.class) != null) {
              // kill zombie
              Gdx.app.log(TAG, "Killed zombie " + entity.getComponent(TextComponent.class).text);
              engine.removeEntity(entity2);
              engine.removeEntity(entity);

              engine.addEntity(createBloodEntity(entity));
            }
          }
        }
      }
    }
  }

  private Entity createBloodEntity(Entity entity) {
    // Save the transform component
    TransformComponent transform = entity.getComponent(TransformComponent.class);
    ParticleComponent particle = new ParticleComponent();
    ParticleEffect particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("blood.particle"), Gdx.files.internal(""));
    particleEffect.setPosition(transform.position.x, transform.position.y);
    particleEffect.start();

    particle.particleEffect = particleEffect;

    Entity bloodEntity = new Entity();

    bloodEntity.add(particle);

    return bloodEntity;
  }

}
