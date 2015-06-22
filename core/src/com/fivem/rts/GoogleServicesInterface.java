package com.fivem.rts;

/**
 * Hides all Google Player Services implementations so it can be used
 * across platforms.
 */
public interface GoogleServicesInterface {
  void signin();
  void signout();

  /**
   * Creates a game with two players together
   */
  void automatch();

  /**
   * Send message to all parties in the room
   * @param message
   */
  void broadcastMessage(byte[] message);

  /**
   * Received a message from a room
   * @param playerId Identifier of the participant
   * @param message Message Payload
   */
  void receiveMessage(String playerId, byte[] message);
}
