package com.fivem.rts.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fivem.rts.component.BoundsComponent;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.TransformComponent;

public class BoundsSystem extends IteratingSystem {

  private ComponentMapper<BoundsComponent> boundsMapper;
  private ComponentMapper<TransformComponent> transformMapper;

  public BoundsSystem() {
    super(Family.all(BoundsComponent.class, MovementComponent.class).get());

    boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
    transformMapper = ComponentMapper.getFor(TransformComponent.class);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BoundsComponent boundsComponent = boundsMapper.get(entity);
    TransformComponent transformComponent = transformMapper.get(entity);

    boundsComponent.bounds.x = transformComponent.position.x + boundsComponent.bounds.width * 0.5f;
    boundsComponent.bounds.y = transformComponent.position.y + boundsComponent.bounds.height * 0.5f;
  }

}
