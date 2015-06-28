package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.fivem.rts.GameSync;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.World;
import com.fivem.rts.command.AckCommand;
import com.fivem.rts.command.Command;
import com.fivem.rts.command.MoveCommand;
import com.fivem.rts.component.DestinationComponent;
import com.fivem.rts.component.SelectionComponent;
import com.fivem.rts.network.CommandNetwork;

import java.util.ArrayList;

public class CommandReadSystem extends EntitySystem {

  private static final String TAG = CommandReadSystem.class.getSimpleName();

  private final CommandNetwork commandNetwork;
  private final GameSync sync;
  private Engine engine;
  private boolean isWorldProcessing = true;
  private boolean resetWorldNextIteration = false;

  public CommandReadSystem(CommandNetwork commandNetwork, GameSync sync) {
    this.commandNetwork = commandNetwork;
    this.sync = sync;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  private void setWorldProcessing(boolean processing) {
    if (processing == isWorldProcessing) {
      return;
    }

    if (processing == false) {
      Gdx.app.log(TAG, "STOP processing, blocked at frame: " + sync.getFrame());
    } else {
      Gdx.app.log(TAG, "CONTINUE world at frame: " + sync.getFrame());
    }


    isWorldProcessing = processing;

    // These systems actually affect the world entities
    MovementSystem movementSystem = engine.getSystem(MovementSystem.class);
    ParticleSystem particleSystem = engine.getSystem(ParticleSystem.class);
    CollisionSystem collisionSystem = engine.getSystem(CollisionSystem.class);
    ShootingSystem shootingSystem = engine.getSystem(ShootingSystem.class);

    // Stop this class because it sends the ack frames (and calls finishFrame()
    CommandWriteSystem commandWriteSystem = engine.getSystem(CommandWriteSystem.class);

    movementSystem.setProcessing(processing);
    particleSystem.setProcessing(processing);
    collisionSystem.setProcessing(processing);
    shootingSystem.setProcessing(processing);
    commandWriteSystem.setProcessing(processing);
  }

  @Override
  public void update(float deltaTime) {

    // Required because you can't remove all entities, and add entities in the same iteration
    // (RemoveAll is a post operation)
    if (resetWorldNextIteration) {
      SpaceRtsGame.world.resetWorld(engine);
      resetWorldNextIteration = false;
      return;
    }

    ArrayList<Command> commands = commandNetwork.receiveCommands();

    ArrayList<Command> currentFrameCommands = sync.startFrame(commands);

    if (currentFrameCommands == null) {
      // Freeze here, but how can you considering that this is a system in the engine, and you don't want other systems
      // to run.
      setWorldProcessing(false);
      return;
    } else {
      setWorldProcessing(true);
    }

    for (Command command : currentFrameCommands) {
      if (command == null) {
        return;
      }
      Gdx.app.log(TAG, "Running: " + command.toString());

      switch(command.type) {
        case START:
          SpaceRtsGame.gameStatus = "Received Start Game.. Reinit world";
          sync.reset(SpaceRtsGame.NUM_PLAYERS);
          engine.removeAllEntities();
          resetWorldNextIteration = true;
          setWorldProcessing(false);
          break;
        case ACK:
          AckCommand ack = command.ackCommand;
          break;

        case MOVE:

          MoveCommand moveCommand = command.moveCommand;

          ImmutableArray<Entity> selectableEntities = engine.getEntitiesFor(Family.all(SelectionComponent.class).get());
          for (Entity entity : selectableEntities) {
            if (!moveCommand.entityUuids.contains(entity.getId(), true)) {
              continue;
            }
            SelectionComponent selection = entity.getComponent(SelectionComponent.class);
            if (selection.selected) {
              DestinationComponent destination = new DestinationComponent();
              destination.position.set(moveCommand.destination.x, moveCommand.destination.y);
              entity.add(destination);
            }
          }
          break;
        case ROOM_CONNECTED:
          Gdx.app.log(TAG, "Room connected: command system engaged!");
          break;

        default:
          Gdx.app.error(TAG, "Processing Unknown command " + command);

      }
    }
  }
}
