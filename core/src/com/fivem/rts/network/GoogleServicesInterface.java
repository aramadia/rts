package com.fivem.rts.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Hides all Google Player Services implementations so it can be used
 * across platforms.
 */
public interface GoogleServicesInterface {
  void signin();
  void signout();

  /**
   * Creates a game with two players together.
   * The sequence to start a game is:
   * 1. automatch() - black magic, will async connect two users together
   * 2. receive RoomConnect message.
   * 3. All players send ready sync ping.
   * 4. Game starts.
   */
  void automatch();

  class GoogleRoom {
    public String myId;
    public ArrayList<String> participantIds = new ArrayList<String>();
  }

  /**
   * Poll to determine if room has been created
   * @return null if no room has been formed
   */
  GoogleRoom connected();

  /**
   * Send message to all parties in the room (including yourself)
   * @param message
   */
  void broadcastMessage(byte[] message);

  class Message {
    //Identifier of the sending participant
    public String playerId;

    // Message Payload
    public byte[] message;
  }

  /**
   * Receive all messages from the room since being called.
   */
  List<Message> receiveMessages();
}
