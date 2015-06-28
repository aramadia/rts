package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.component.*;

public class CollisionSystem extends EntitySystem {

  private static final String TAG = CollisionSystem.class.getSimpleName();

  private ImmutableArray<Entity> entities;
  private Engine engine;

  public CollisionSystem() {
    super();
  }

  // Moved here from update loop because it was causing too many allocations.
  Vector2[] screenCoords = {
      new Vector2(0, 0),
      new Vector2(SpaceRtsGame.SCENE_WIDTH, 0),
      new Vector2(SpaceRtsGame.SCENE_WIDTH, SpaceRtsGame.SCENE_HEIGHT),
      new Vector2(0, SpaceRtsGame.SCENE_HEIGHT)
  };

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
    entities = engine.getEntitiesFor(Family.all(BoundsComponent.class, MovementComponent.class, TransformComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    ImmutableArray<Entity> screenBoundEntities = engine.getEntitiesFor(
        Family.all(BoundsComponent.class, MovementComponent.class, TransformComponent.class)
            .exclude(BulletComponent.class).get());

    for (Entity screenBoundEntity : screenBoundEntities) {
      BoundsComponent bounds = screenBoundEntity.getComponent(BoundsComponent.class);
      MovementComponent movement = screenBoundEntity.getComponent(MovementComponent.class);



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
      for (int j = i + 1; j < entities.size(); j++) {
        entity2 = entities.get(j);
        bounds = entity.getComponent(BoundsComponent.class);
        bounds2 = entity2.getComponent(BoundsComponent.class);
        if (bounds.polygon.getBoundingRectangle().overlaps(bounds2.polygon.getBoundingRectangle())
            && Intersector.overlapConvexPolygons(bounds.polygon, bounds2.polygon)) {
          if (entity.getComponent(BulletComponent.class) != null) {
            if (entity2.getComponent(ZombieComponent.class) != null) {
              handleCollision(entity2, entity);
            }
          } else if (entity.getComponent(ZombieComponent.class) != null) {
            if (entity2.getComponent(BulletComponent.class) != null) {
              handleCollision(entity, entity2);
            }
          }
        }
      }
    }
  }

  /**
   * Applies damage and kills zombie if applicable
   *
   * @param zombie must contain ZombieComponent
   * @param bullet must contain BulletComponent
   */
  private void handleCollision(Entity zombie, Entity bullet) {
    ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
    TextComponent zombieTextComponent = zombie.getComponent(TextComponent.class);
    BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);

    zombieComponent.health -= bulletComponent.damage;

//    Gdx.app.log(TAG, zombieTextComponent.text + " has " + zombieComponent.health + ".");

    engine.removeEntity(bullet);

    if (zombieComponent.health <= 0) {
      // kill the zombie
      Gdx.app.debug(TAG, zombieTextComponent.text + " was killed.");
      engine.removeEntity(zombie);
      engine.addEntity(createBloodEntity(zombie));
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
