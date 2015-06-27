package com.fivem.rts.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.Serializable;

/**
 * Contains all commands, but only one is set.
 * Use CommandType to figure out which command is set
 * TODO Use proper serialization so we don't need this crap.  (NOt sure how to do json inheritance)
    */
  public class Command {
  public enum Type {
    NONE,
    MOVE,
    START,
    ROOM_CONNECTED,
    ACK,

  }

  public MoveCommand moveCommand;
  public StartCommand startCommand;
  public RoomConnectedCommand roomConnectedCommand;
  public AckCommand ackCommand;
  public Type type;


  public Command() {
  }

  public static Command moveCommand(MoveCommand cmd) {
    Command command = new Command();
    command.moveCommand = cmd;
    command.type = Type.MOVE;
    return command;
  }


  public static Command roomConnectedCommand(RoomConnectedCommand cmd) {
    Command command = new Command();
    command.roomConnectedCommand = cmd;
    command.type = Type.ROOM_CONNECTED;
    return command;
  }

  public static Command startCommand(String name) {
    Command command = new Command();
    command.startCommand = new StartCommand();
    command.startCommand.name = name;
    command.type = Type.START;
    return command;
  }

  public static Command ackCommand(AckCommand cmd) {
    Command command = new Command();
    command.ackCommand = cmd;
    command.type = Type.ACK;
    return command;
  }

  public BaseCommand getCommand() {
    switch(type) {
      case MOVE:
        return moveCommand;
      case START:
        return startCommand;
      case ROOM_CONNECTED:
        return roomConnectedCommand;
      case ACK:
        return ackCommand;
      default:
        return null;
    }
  }

  @Override
  public String toString() {
    BaseCommand cmd = getCommand();
    if (cmd == null) {
      return "NULL command";
    }
    return type.toString() + ": " + getCommand().toString();
  }
}


