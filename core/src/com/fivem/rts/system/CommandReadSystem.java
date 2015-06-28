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

  public CommandReadSystem(CommandNetwork commandNetwork, GameSync sync) {
    this.commandNetwork = commandNetwork;
    this.sync = sync;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  private void setWorldProcessing(boolean processing) {
    // These systems actually affect the world entities
    MovementSystem movementSystem = engine.getSystem(MovementSystem.class);
    ParticleSystem particleSystem = engine.getSystem(ParticleSystem.class);
    CollisionSystem collisionSystem = engine.getSystem(CollisionSystem.class);
    ShootingSystem shootingSystem = engine.getSystem(ShootingSystem.class);

    movementSystem.setProcessing(processing);
    particleSystem.setProcessing(processing);
    collisionSystem.setProcessing(processing);
    shootingSystem.setProcessing(processing);
  }

  @Override
  public void update(float deltaTime) {
    // TODO take time into account

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
          SpaceRtsGame.gameStatus = "Starting Game";
          sync.reset(SpaceRtsGame.NUM_PLAYERS);
          engine.removeAllEntities();
          SpaceRtsGame.world.resetWorld(engine);
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
          Gdx.app.log(TAG, "Processing Unknown command " + command);

      }
    }
  }
}
