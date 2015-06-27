package com.fivem.rts.command;

/**
 * Do not use this class outside the command package.  Use Command
 * Created by Daniel on 6/25/2015.
 */
public class BaseCommand {

  // The frame this command should execute, -1 for an unsynced command (only acks)
  // An int is use because at 60fps, we can have a ~1 year game
  public int syncTime = -1;
}
