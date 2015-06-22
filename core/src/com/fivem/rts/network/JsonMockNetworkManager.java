package com.fivem.rts.network;

import com.badlogic.gdx.utils.Json;
import com.fivem.rts.MoveCommand;

public class JsonMockNetworkManager implements NetworkManager {

  private Json json = new Json();

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
