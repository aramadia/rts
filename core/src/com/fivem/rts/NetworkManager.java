package com.fivem.rts;

public interface NetworkManager {

  MoveCommand receiveCommand();
  void sendCommand(MoveCommand moveCommand);

}
