package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.component.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Hashes all entities and their components to generate a hash code
 */
public class HashingSystem extends EntitySystem {

  private Engine engine;

  private final List<ComponentMapper> hashMappers;



  public HashingSystem() {
    hashMappers = new ArrayList<ComponentMapper>();

    Class hashClass[] = {
        BoundsComponent.class,
        BulletComponent.class,
        DestinationComponent.class,
        GunnerComponent.class,
        MovementComponent.class,
        TransformComponent.class,
        ZombieComponent.class};

    for (Class c : hashClass) {
      hashMappers.add(ComponentMapper.getFor(c));
    }
  }

  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    this.engine = engine;
  }



  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    int hashCode = 1;

    ImmutableArray<Entity> entities = engine.getEntities();
    for (int i = 0; i < entities.size(); i++) {
      Entity entity = entities.get(i);

      // Hash the entity index and its uuid
      hashCode = 37 * hashCode +  (int)entity.getId() * (i * 1000 % 397);

      for (ComponentMapper mapper : hashMappers) {
        Component component = mapper.get(entity);
        if (component != null) {
          hashCode = 37 * hashCode + component.hashCode();
        }
      }
    }

    SpaceRtsGame.sync.setHash(hashCode);
  }
}
