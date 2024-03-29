package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fivem.rts.component.BulletComponent;

public class ParticleSystem extends IteratingSystem {

  private ComponentMapper<BulletComponent> particleMapper;
  private Engine engine;

  public ParticleSystem() {
    super(Family.all(BulletComponent.class).get());

    particleMapper = ComponentMapper.getFor(BulletComponent.class);
  }

  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    this.engine = engine;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BulletComponent particle = particleMapper.get(entity);

    particle.lifespan -= deltaTime;
    if (particle.lifespan <= 0) {
      engine.removeEntity(entity);
    }
  }

}
