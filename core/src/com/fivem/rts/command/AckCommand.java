package com.fivem.rts.command;

public class AckCommand extends BaseCommand {
  // We are finished sending commands that are to be executed at this frame.
  // Clients can execute any frames from this client <= frameReady
  public int frameReady;

  @Override
  public String toString() {
    return "Ready: " + frameReady;
  }
}
