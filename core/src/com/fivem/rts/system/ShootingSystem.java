package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fivem.rts.SpaceRtsGame;
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

    gunner.reloadProgress -= deltaTime;
    if (gunner.reloadProgress < 0) {
      gunner.reloadProgress = 0.0f;
    }

    if (gunner.reloadProgress == 0) {
      // Shoot a bullet

      // TODO wrap in a factory
      Entity b = new Entity();
      TransformComponent bTransform = new TransformComponent();
      MovementComponent bMovement = new MovementComponent();
      BulletComponent bParticle = new BulletComponent();
      BoundsComponent bBounds = new BoundsComponent();
      bTransform.position.set(transform.position);
      bMovement.velocity.set(SpaceRtsGame.random.nextFloat() * (1 - (-1)) + (-1), SpaceRtsGame.random.nextFloat() * (1 - (-1)) + (-1));
      bMovement.velocity.nor();
      bMovement.velocity.scl(gunner.bulletSpeed);
      bBounds.setBoundsFromRect(bTransform.position.x, bTransform.position.y, bParticle.size, bParticle.size);
      b.add(bTransform);
      b.add(bMovement);
      b.add(bParticle);
      b.add(bBounds);

      engine.addEntity(b);

      // Reset the bullet counter
      gunner.reloadProgress = gunner.reloadTime;
    }
  }

}
