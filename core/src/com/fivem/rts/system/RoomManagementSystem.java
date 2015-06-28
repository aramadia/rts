package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.fivem.rts.GameSync;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.command.AckCommand;
import com.fivem.rts.command.Command;
import com.fivem.rts.command.MoveCommand;
import com.fivem.rts.command.StartCommand;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.SelectionComponent;
import com.fivem.rts.network.CommandNetwork;
import com.fivem.rts.network.GoogleCommandNetwork;
import com.fivem.rts.network.GoogleServicesInterface;

import java.util.ArrayList;

public class RoomManagementSystem extends EntitySystem {

  private static final String TAG = RoomManagementSystem.class.getSimpleName();

  private final GoogleServicesInterface googleServicesInterface;
  private final GameSync sync;
  private final CommandNetwork commandNetwork;
  private Engine engine;
  private GoogleServicesInterface.GoogleRoom googleRoom;

  public RoomManagementSystem(GoogleServicesInterface googleServicesInterface, CommandNetwork commandNetwork, GameSync sync) {
    this.googleServicesInterface = googleServicesInterface;
    this.sync = sync;
    this.commandNetwork = commandNetwork;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  @Override
  public void update(float deltaTime) {

    if (googleRoom != googleServicesInterface.connected()) {
      Gdx.app.log(TAG, "Connected, sending start command");
      SpaceRtsGame.gameStatus = "Connected to Room";
      StartCommand start = new StartCommand();
      commandNetwork.sendCommand(Command.startCommand(start));

    }
  }
}
