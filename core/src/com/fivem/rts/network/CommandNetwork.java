package com.fivem.rts.network;

import com.fivem.rts.command.Command;

import java.util.ArrayList;

/**
 * Represents a higher level abstraction of a send/receive commands to a room
 */
public interface CommandNetwork {

  /**
   * Call to obtain list of commands queued since last call.
   * @return
   */
  ArrayList<Command> receiveCommands();

  /**
   * Call to broadcast commands over the network.
   * Note: This adds it to its own command queue, since you don't send commands to yourself.
   * @param command
   */
  void sendCommand(Command command);

}
