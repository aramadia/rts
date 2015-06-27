package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.fivem.rts.GameSync;
import com.fivem.rts.command.Command;
import com.fivem.rts.command.MoveCommand;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.SelectionComponent;
import com.fivem.rts.network.CommandNetwork;

import java.util.ArrayList;

public class CommandWriteSystem extends EntitySystem {

  private static final String TAG = CommandWriteSystem.class.getSimpleName();

  private final CommandNetwork commandNetwork;
  private final GameSync sync;
  private Engine engine;

  public CommandWriteSystem(CommandNetwork commandNetwork, GameSync sync) {
    this.commandNetwork = commandNetwork;
    this.sync = sync;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  @Override
  public void update(float deltaTime) {
    Command ack = sync.finishFrame();
    commandNetwork.sendCommand(ack);
  }
}
