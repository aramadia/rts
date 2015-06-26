package com.fivem.rts.command;


import java.util.ArrayList;

/**
 * Created by Daniel on 6/25/2015.
 */
public class RoomConnectedCommand extends BaseCommand{
  public String myId;
  public ArrayList<String> participants;

  @Override
  public String toString() {
    return "myId: " + myId + "Participants; " +  participants.toString();
  }
}
