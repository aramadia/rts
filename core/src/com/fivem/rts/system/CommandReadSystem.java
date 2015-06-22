package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.fivem.rts.CommandManager;
import com.fivem.rts.MoveCommand;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.SelectionComponent;

public class CommandReadSystem extends EntitySystem {

  private final CommandManager commandManager;
  private Engine engine;

  public CommandReadSystem(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  @Override
  public void update(float deltaTime) {
    // TODO take time into account

    MoveCommand moveCommand = commandManager.getCommand();
    if (moveCommand == null) {
      return;
    }

    ImmutableArray<Entity> selectableEntities = engine.getEntitiesFor(Family.all(SelectionComponent.class).get());
    for (Entity entity : selectableEntities) {
      if (!moveCommand.entityUuids.contains(entity.getId(), true)) {
        continue;
      }
      SelectionComponent selection = entity.getComponent(SelectionComponent.class);
      if (selection.selected) {
        DestinationComponent destination = new DestinationComponent();
        destination.position.set(moveCommand.destination.x, moveCommand.destination.y);
        entity.add(destination);
      }
    }
  }
}
