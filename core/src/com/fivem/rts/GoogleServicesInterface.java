package com.fivem.rts;

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

  /**
   * Send message to all parties in the room
   * @param message
   */
  void broadcastMessage(byte[] message);

  /**
   * Received a message from a room
   * @param playerId Identifier of the sending participant
   * @param message Message Payload
   */
  void receiveMessage(String playerId, byte[] message);
}
