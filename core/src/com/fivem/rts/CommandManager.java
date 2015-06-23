package com.fivem.rts;

import com.fivem.rts.network.NetworkManager;

import java.util.ArrayList;

public class CommandManager {

  private NetworkManager networkManager;
  private MoveCommand command;

  public CommandManager(NetworkManager networkManager) {
    this.networkManager = networkManager;
  }

  public void addCommand(MoveCommand command) {
    this.command = command;
  }

  public ArrayList<MoveCommand> getCommands() {
    return networkManager.receiveCommands();
  }

  public void sendCommands() {
    networkManager.sendCommand(command);
    command = null;
  }

}
