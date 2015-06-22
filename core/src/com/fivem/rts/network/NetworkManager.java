package com.fivem.rts.network;

import com.fivem.rts.MoveCommand;

public interface NetworkManager {

  MoveCommand receiveCommand();
  void sendCommand(MoveCommand moveCommand);

}
