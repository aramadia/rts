package com.fivem.rts.command;

/**
 * Created by Daniel on 6/26/2015.
 */
public class AckCommand extends BaseCommand {
  // We are finished sending commands that are to be executed at this frame.
  // Clients can execute any frames from this client <= frameReady
  public int frameReady;
}
