package com.fivem.rts.command;


import java.util.ArrayList;

public class RoomConnectedCommand extends BaseCommand{
  public String myId;
  public ArrayList<String> participants;

  @Override
  public String toString() {
    return "myId: " + myId + "Participants; " +  participants.toString();
  }
}
