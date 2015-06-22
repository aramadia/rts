package com.fivem.rts.android;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.WindowManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fivem.rts.GoogleServicesInterface;
import com.fivem.rts.SpaceRts;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.*;
import com.google.example.games.basegameutils.GameHelper;
import com.google.android.gms.games.Games;

import java.util.ArrayList;


public class AndroidLauncher extends AndroidApplication implements GoogleServicesInterface, RoomUpdateListener,
        RealTimeMessageReceivedListener {

  private static final String TAG = "AndroidImpl";
  GameHelper gameHelper;

  String roomId;
  String myParticipantId;
  private ArrayList<Participant> participants;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new SpaceRts(this), config);

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

  @Override
  public void onRoomCreated(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
    }

    Gdx.app.log(TAG, "onRoomCreated!");

  }

  @Override
  public void onJoinedRoom(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
    }

    Gdx.app.log(TAG, "onJoinedRoom: This means the client joined but still waiting for more players");
  }

  @Override
  public void onRoomConnected(int statusCode, Room room) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      // let screen go to sleep
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      // show error message, return to main screen.
      Gdx.app.error(TAG, "onRoomConnected" + statusCode);
    }

    Gdx.app.log(TAG, "Everyone connected to room, lets start the game");


    roomId = room.getRoomId();
    participants = room.getParticipants();
    myParticipantId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));

    Gdx.app.log(TAG, "My participant Id " + myParticipantId);

    String msg = "Hi, I connected to the room " + myParticipantId;
    broadcastMessage(msg.getBytes());
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
  }

  @Override
  public void broadcastMessage(byte[] message) {
    for (Participant p : participants) {
        Games.RealTimeMultiplayer.sendReliableMessage(gameHelper.getApiClient(), null, message,
                roomId, p.getParticipantId());
    }
  }

}

