package com.fivem.rts.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fivem.rts.network.GoogleServicesInterface;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.system.ConsoleSystem;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.*;
import com.google.example.games.basegameutils.GameHelper;


import java.util.ArrayList;
import java.util.List;


public class AndroidLauncher extends AndroidApplication implements GoogleServicesInterface, RoomUpdateListener,
        RealTimeMessageReceivedListener, RealTimeMultiplayer.ReliableMessageSentCallback {

  private static final String TAG = AndroidLauncher.class.getSimpleName();
  GameHelper gameHelper;

  String roomId;
  String myParticipantId;
  private GoogleRoom googleRoom;
  private ArrayList<Participant> participants = new ArrayList<Participant>();

  private ArrayList<GoogleServicesInterface.Message> queuedMessages = new ArrayList<Message>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new SpaceRtsGame(this), config);

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
    Gdx.app.log(TAG, "onRoomCreated " + room.getRoomId());

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

    Gdx.app.log(TAG, "onRoomConnected: Starting game with room " + room.getRoomId());


    roomId = room.getRoomId();
    participants = room.getParticipants();
    myParticipantId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));

    Gdx.app.log(TAG, "My participant Id " + myParticipantId);

    googleRoom = new GoogleRoom();
    googleRoom.myId = myParticipantId;
    for (Participant p : participants) {
      googleRoom.participantIds.add(p.getParticipantId());
    }

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
  public GoogleRoom connected() {
    return googleRoom;
  }

  // Synchronize access to queuedMessages
  @Override
  public synchronized void onRealTimeMessageReceived(RealTimeMessage rtm) {
    Message msg = new Message();
    msg.playerId = rtm.getSenderParticipantId();
    msg.message = rtm.getMessageData();

    queuedMessages.add(msg);
  }

  // Synchronize access to queuedMessages
  @Override
  public synchronized List<Message> receiveMessages() {
    ArrayList<Message> temp = new ArrayList<>(queuedMessages);
    queuedMessages.clear();
    return temp;
  }

  @Override
  public void broadcastMessage(byte[] message) {
    for (Participant p : participants) {
      if (!p.getParticipantId().equals(myParticipantId)) {
//        Gdx.app.log(TAG, myParticipantId + " sending to " + p.getParticipantId());
         Games.RealTimeMultiplayer.sendReliableMessage(gameHelper.getApiClient(), this, message,
            roomId, p.getParticipantId());
      }
    }
  }

  @Override
  public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
    if (statusCode != GamesStatusCodes.STATUS_OK) {
      Gdx.app.error(TAG, "Failed to send message " + statusCode);
    }
  }


  @Override
  public void debug(String tag, String message) {
    super.debug(tag, message);
    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.debug(tag, message);
    }
  }

  @Override
  public void debug(String tag, String message, Throwable exception) {
    super.debug(tag, message, exception);
    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.debug(tag, message, exception);
    }
  }

  @Override
  public void log(String tag, String message) {
    super.log(tag, message);
    if (logLevel >= LOG_INFO) {
      ConsoleSystem.log(tag, message);
    }
  }

  @Override
  public void log(String tag, String message, Throwable exception) {
    super.log(tag, message, exception);
    if (logLevel >= LOG_INFO) {
      ConsoleSystem.log(tag, message, exception);
    }
  }

  @Override
  public void error(String tag, String message) {
    super.error(tag, message);
    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.error(tag, message);
    }
  }

  @Override
  public void error(String tag, String message, Throwable exception) {
    super.error(tag, message, exception);
    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.error(tag, message, exception);
    }
  }


}

