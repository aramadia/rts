package com.fivem.rts.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.fivem.rts.command.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of network manager using GoogleServiceInterface
 */
public class GoogleCommandNetwork implements CommandNetwork {
  private GoogleServicesInterface network;
  private ArrayList<Command> queuedCommands = new ArrayList<Command>();
  private Json json = new Json();
  private final static String TAG = "GoogleCommandNetwork";


  public GoogleCommandNetwork(GoogleServicesInterface network) {
    this.network = network;
  }

  @Override
  public ArrayList<Command> receiveCommands() {
    List<GoogleServicesInterface.Message> messages = network.receiveMessages();

    for (GoogleServicesInterface.Message m: messages) {
      String s = new String(m.message);
      queuedCommands.add(json.fromJson(Command.class, s));
    }

    ArrayList<Command> temp = new ArrayList<Command>(queuedCommands);
    queuedCommands.clear();
    return temp;
  }

  @Override
  public void sendCommand(Command command) {
    if (command == null) {
      return;
    }
    String serializedCommand = json.toJson(command);
    Gdx.app.log(TAG, "Sending command " + serializedCommand);
    network.broadcastMessage(serializedCommand.getBytes());

    // Since you don't get broadcasted messages, add it to the queue here.
    queuedCommands.add(command);
  }
}
