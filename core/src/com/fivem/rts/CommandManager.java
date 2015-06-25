package com.fivem.rts;

import com.fivem.rts.network.NetworkManager;

import java.util.ArrayList;

public class CommandManager {

  private NetworkManager networkManager;
  private Command command;

  public CommandManager(NetworkManager networkManager) {
    this.networkManager = networkManager;
  }

  public void addCommand(Command command) {
    this.command = command;
  }

  public ArrayList<Command> getCommands() {
    return networkManager.receiveCommands();
  }

  public void sendCommands() {
    networkManager.sendCommand(command);
    command = null;
  }

}
