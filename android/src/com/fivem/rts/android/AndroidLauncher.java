package com.fivem.rts.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Json;
import com.fivem.rts.Command;
import com.fivem.rts.GoogleServicesInterface;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.network.NetworkManager;
import com.fivem.rts.system.ConsoleSystem;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.*;
import com.google.example.games.basegameutils.GameHelper;


import java.util.ArrayList;


public class AndroidLauncher extends AndroidApplication implements GoogleServicesInterface, RoomUpdateListener,
        RealTimeMessageReceivedListener, NetworkManager {

  private static final String TAG = "AndroidImpl";
  GameHelper gameHelper;

  String roomId;
  String myParticipantId;
  private ArrayList<Participant> participants = new ArrayList<Participant>();
  Json json = new Json();
  private ArrayList<Command> queuedCommands = new ArrayList<Command>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new SpaceRtsGame(this, this), config);

    gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
    gameHelper.enableDebugLog(true);

    GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
      @Override
      public void onSignInSucceeded() {
        Gdx.app.log(TAG, "sign in succeeded");
        automatch();
      }

      @Override
      public void onSignInFailed() {
        Gdx.app.log(TAG, "sign in failed");
      }
    };

    gameHelper.setup(gameHelperListener);
  }

  @Override
  protected void onStart() {
    super.onStart();
    gameHelper.onStart(this);
  }

  @Override
  protected void onStop() {
    super.onStop();

    Gdx.app.log(TAG, "leaving room");
    Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(), this, roomId);

    gameHelper.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    gameHelper.onActivityResult(requestCode, resultCode, data);

  }

  @Override
  public void signin() {

  }

  @Override
  public void signout() {

  }

  public void error(String msg, int statusCode) {

  }

  @Override
  public void onRoomCreated(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
      Gdx.app.error(TAG, "onRoomCreated " + GamesStatusCodes.getStatusString(statusCode));
      return;
    }

    roomId = room.getRoomId();
    Gdx.app.log(TAG, "onRoomCreated "+ room.getRoomId());

  }

  @Override
  public void onJoinedRoom(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
      Gdx.app.error(TAG, "onJoinedRoom" + statusCode);
    }

    Gdx.app.log(TAG, "onJoinedRoom: " + room.getRoomId() + " This means the client joined but still waiting for more players");
  }

  @Override
  public void onRoomConnected(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
      Gdx.app.error(TAG, "onRoomConnected" + statusCode);
    }

    Gdx.app.log(TAG, "Starting game with room <" + room.getRoomId() + ">");


    roomId = room.getRoomId();
    participants = room.getParticipants();
    myParticipantId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));

    Gdx.app.log(TAG, "My participant Id " + myParticipantId);

//    String msg = "Hi, I connected to the room " + myParticipantId;
    //broadcastMessage(msg.getBytes());
  }

  @Override
  public void onLeftRoom(int statusCode, String roomId) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
      Gdx.app.error(TAG, "onLeftRoom" + statusCode);
    }

    Gdx.app.log(TAG, "onLeftRoom");
  }


  // create a RoomConfigBuilder that's appropriate for your implementation
  private RoomConfig.Builder makeBasicRoomConfigBuilder() {
    return RoomConfig.builder(this)
            .setMessageReceivedListener(this);

    // TODO(dalam): Add the room status update listener to detect room change events.
  }

  @Override
  public void automatch() {
    // Auto-match criteria to invite one random automatch opponent.
    Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

    // build the room config:
    RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
    roomConfigBuilder.setAutoMatchCriteria(am);
    RoomConfig roomConfig = roomConfigBuilder.build();

    // create room:
    Games.RealTimeMultiplayer.create(gameHelper.getApiClient(), roomConfig);

    // prevent screen from sleeping during handshake
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  @Override
  public void onRealTimeMessageReceived(RealTimeMessage rtm) {
    receiveMessage(rtm.getSenderParticipantId(), rtm.getMessageData());
  }

  @Override
  public void receiveMessage(String playerId, byte[] message) {
    String s = new String(message);
    Gdx.app.log(TAG, "received " + s + " from " + playerId);

    queuedCommands.add(json.fromJson(Command.class, s));
  }

  @Override
  public void broadcastMessage(byte[] message) {
    for (Participant p : participants) {
//      if (!p.getParticipantId().equals(myParticipantId)) {
        Gdx.app.log(TAG, myParticipantId + " sending to " + p.getParticipantId());
        Games.RealTimeMultiplayer.sendReliableMessage(gameHelper.getApiClient(), null, message,
            roomId, p.getParticipantId());
//      }
    }
  }

  @Override
  public ArrayList<Command> receiveCommands() {
    ArrayList<Command> temp = new ArrayList<Command>(queuedCommands);
    queuedCommands.clear();
    return temp;
  }

  @Override
  public void sendCommand(Command command) {
    if (command == null) {
      return;
    }
    String serializedCommand = json.toJson(command);
    Gdx.app.log(TAG, "Sending command " + serializedCommand);
    broadcastMessage(serializedCommand.getBytes());

    // Since you don't get broadcasted messages, add it to the queue here.
    queuedCommands.add(command);
  }

  @Override
  public void log(String tag, String message) {
    super.log(tag, message);
    ConsoleSystem.addLog(tag, message);
  }

  @Override
  public void log(String tag, String message, Throwable exception) {
    super.log(tag, message, exception);
    ConsoleSystem.addLog(tag, message);
  }
}

