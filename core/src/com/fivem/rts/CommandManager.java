package com.fivem.rts;

import com.badlogic.gdx.utils.Json;

public class CommandManager {

  private NetworkManager networkManager;
  private MoveCommand command;

  public CommandManager(NetworkManager networkManager) {
    this.networkManager = networkManager;
  }

  public void addCommand(MoveCommand command) {
    this.command = command;
  }

  public MoveCommand getCommand() {
    return networkManager.receiveCommand();
  }

  public void sendCommands() {
    networkManager.sendCommand(command);
  }

  public static class NetworkManager {

    private Json json;

    public NetworkManager(Json json) {
      this.json = json;
    }

    private String serializedCommand;

    public MoveCommand receiveCommand() {
      if (serializedCommand != null) {
        return json.fromJson(MoveCommand.class, serializedCommand);
      }

      return null;
    }

    public void sendCommand(MoveCommand moveCommand) {
      serializedCommand = json.toJson(moveCommand);
    }

  }


}
