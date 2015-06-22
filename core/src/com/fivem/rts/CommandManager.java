package com.fivem.rts;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public class CommandManager {

  private Array<MoveCommand> commands = new Array<MoveCommand>();

  public void addCommand(MoveCommand command) {
    commands.add(command);
  }

  public ImmutableArray<MoveCommand> getCommands() {
    // TODO read from network
    ImmutableArray<MoveCommand> moveCommands = new ImmutableArray<MoveCommand>(commands);
    commands = new Array<MoveCommand>();
//    commands.clear();
    return moveCommands;
  }

  public void sendCommands() {
    for (MoveCommand command : commands) {
      System.out.println("sending command = " + command);
    }
    // TODO write to network
  }
}
