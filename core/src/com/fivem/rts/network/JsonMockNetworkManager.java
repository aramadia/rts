package com.fivem.rts.network;

import com.badlogic.gdx.utils.Json;
import com.fivem.rts.MoveCommand;

import java.util.ArrayList;

public class JsonMockNetworkManager implements NetworkManager {

  private Json json = new Json();

  private String serializedCommand;

  @Override
  public ArrayList<MoveCommand> receiveCommands() {
    ArrayList<MoveCommand> commands = new ArrayList<MoveCommand>();
    if (serializedCommand != null) {
      commands.add(json.fromJson(MoveCommand.class, serializedCommand));
    }

    return commands;
  }

  @Override
  public void sendCommand(MoveCommand moveCommand) {
    serializedCommand = json.toJson(moveCommand);
  }

}
