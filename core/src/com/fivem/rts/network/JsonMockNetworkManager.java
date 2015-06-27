package com.fivem.rts.network;

import com.badlogic.gdx.utils.Json;
import com.fivem.rts.command.Command;

import java.util.ArrayList;

public class JsonMockNetworkManager implements CommandNetwork {

  private Json json = new Json();

  private String serializedCommand;

  @Override
  public ArrayList<Command> receiveCommands() {
    ArrayList<Command> commands = new ArrayList<Command>();
    if (serializedCommand != null) {
      commands.add(json.fromJson(Command.class, serializedCommand));
    }

    return commands;
  }

  @Override
  public void sendCommand(Command command) {
    serializedCommand = json.toJson(command);
  }

}
