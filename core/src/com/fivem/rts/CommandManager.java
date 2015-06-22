package com.fivem.rts;

import com.fivem.rts.network.NetworkManager;

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
    command = null;
  }

}
