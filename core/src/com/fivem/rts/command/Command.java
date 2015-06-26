package com.fivem.rts.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.Serializable;

/**
 * Contains all commands, but only one is set.
 * Use CommandType to figure out which command is set
 */
public class Command {
  public enum Type {
    NONE,
    MOVE,
    START,

  }

  public MoveCommand moveCommand;
  public StartCommand startCommand;
  public Type type;


  public Command() {
  }

  public static Command moveCommand(MoveCommand cmd) {
    Command command = new Command();
    command.moveCommand = cmd;
    command.type = Type.MOVE;
    return command;
  }

  public BaseCommand getCommand() {
    switch(type) {
      case MOVE:
        return moveCommand;
      case START:
        return startCommand;
      default:
        return null;
    }
  }

  @Override
  public String toString() {
    return type.toString() + ": " + getCommand().toString();
  }
}


