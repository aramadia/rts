package com.fivem.rts.network;

import com.fivem.rts.MoveCommand;

import java.util.ArrayList;


public interface NetworkManager {

  /**
   * Call to obtain list of commands queued since last call.
   * @return
   */
  ArrayList<MoveCommand> receiveCommands();

  /**
   * Call to broadcast commands over the network.
   * Note: This adds it to its own command queue, since you don't send commands to yourself.
   * @param moveCommand
   */
  void sendCommand(MoveCommand moveCommand);

}
