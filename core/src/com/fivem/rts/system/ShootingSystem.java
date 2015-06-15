package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.fivem.rts.component.*;

public class ShootingSystem extends IteratingSystem {

  private final static String TAG = ShootingSystem.class.getSimpleName();

  private ComponentMapper<GunnerComponent> gunnerMapper;
  private ComponentMapper<TransformComponent> transformMapper;

  private Engine engine;

  public ShootingSystem() {
    super(Family.all(GunnerComponent.class).get());

    transformMapper = ComponentMapper.getFor(TransformComponent.class);
    gunnerMapper = ComponentMapper.getFor(GunnerComponent.class);
  }

  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    this.engine = engine;
  }


  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    GunnerComponent gunner = gunnerMapper.get(entity);
    TransformComponent transform = transformMapper.get(entity);

    gunner.reload_progress -= deltaTime;
    if (gunner.reload_progress < 0) {
      gunner.reload_progress = 0.0f;
    }

    if (gunner.reload_progress == 0) {
      // Shoot a bullet

      // TODO wrap in a factory
      Entity b = new Entity();
      TransformComponent bTransform = new TransformComponent();
      MovementComponent bMovement = new MovementComponent();
      ParticleComponent bParticle = new ParticleComponent();
      BoundsComponent bBounds = new BoundsComponent();
      bTransform.position.set(transform.position);
      bMovement.velocity.set(gunner.bullet_speed * (float)Math.random(),gunner.bullet_speed * (float)Math.random());
      bBounds.bounds.set(bTransform.position.x - bParticle.size * 0.5f, bTransform.position.y - bParticle.size * 0.5f, bParticle.size, bParticle.size);
      b.add(bTransform);
      b.add(bMovement);
      b.add(bParticle);
      b.add(bBounds);

      engine.addEntity(b);

      // Reset the bullet counter
      gunner.reload_progress = gunner.reload_time;
    }
  }

}
