package com.fivem.rts.system;

import com.badlogic.ashley.core.EntitySystem;
import com.fivem.rts.CommandManager;
import com.fivem.rts.MoveCommand;

public class CommandWriteSystem extends EntitySystem {

  private final CommandManager commandManager;

  public CommandWriteSystem(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  @Override
  public void update(float deltaTime) {
    // TODO take time into account

    commandManager.sendCommands();
  }
}
