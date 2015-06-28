package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fivem.rts.GameSync;
import com.fivem.rts.command.Command;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.command.MoveCommand;
import com.fivem.rts.component.*;
import com.fivem.rts.network.CommandNetwork;

public class InputSystem extends EntitySystem {

  private static final int ACCELERATION = 200;

  private final OrthographicCamera camera;
  private final CommandNetwork commandNetwork;
  private final GestureDetector gestureDetector;
  private final GameSync sync;

  private Engine engine;

  public InputSystem(OrthographicCamera camera, CommandNetwork commandNetwork, GameSync sync) {
    this.camera = camera;
    this.commandNetwork = commandNetwork;
    this.sync = sync;

    gestureDetector = new CustomGestureDetector(new GestureHandler());
    Gdx.input.setInputProcessor(gestureDetector);
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
  }

  private void unselectAllUnits() {
    ImmutableArray<Entity> selectableEntites = engine.getEntitiesFor(Family.all(SelectionComponent.class).get());
    for (Entity entity : selectableEntites) {
      SelectionComponent selection = entity.getComponent(SelectionComponent.class);
      if (selection.selected) {
        selection.selected = false;
      }
    }
  }

  private void updateAcceleration(int xAcceleration, int yAcceleration) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(Family.all(MovementComponent.class, TransformComponent.class)
            .exclude(BulletComponent.class).get());

    for (Entity entity : entities) {
      entity.getComponent(MovementComponent.class).acceleration.set(xAcceleration, yAcceleration);
    }
  }

  private class CustomGestureDetector extends GestureDetector {
    public CustomGestureDetector(GestureListener listener) {
      super(listener);
    }

    public CustomGestureDetector(float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay, GestureListener listener) {
      super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
    }

    @Override
    public boolean keyDown(int keycode) {
      switch (keycode) {
        case Input.Keys.A:
        case Input.Keys.DPAD_LEFT:
          updateAcceleration(-ACCELERATION, 0);
          return true;
        case Input.Keys.D:
        case Input.Keys.DPAD_RIGHT:
          updateAcceleration(ACCELERATION, 0);
          return true;
        case Input.Keys.W:
        case Input.Keys.DPAD_UP:
          updateAcceleration(0, ACCELERATION);
          return true;
        case Input.Keys.S:
        case Input.Keys.DPAD_DOWN:
          updateAcceleration(0, -ACCELERATION);
          return true;
        case Input.Keys.Z:
          SpaceRtsGame.DEBUG_MODE = !SpaceRtsGame.DEBUG_MODE;
          return true;
        case Input.Keys.TAB:
          ConsoleSystem.CONSOLE_ENABLED = !ConsoleSystem.CONSOLE_ENABLED;
          return true;
        case Input.Keys.ESCAPE:
          unselectAllUnits();
          return true;
      }

      return false;
    }

  }

  private class GestureHandler implements GestureDetector.GestureListener {

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
      Vector3 pos = camera.unproject(new Vector3(x, y, 0));

      ImmutableArray<Entity> selectableEntities =
          engine.getEntitiesFor(Family.all(SelectionComponent.class, BoundsComponent.class).get());

      for (Entity selectableEntity : selectableEntities) {
        if (selectableEntity.getComponent(BoundsComponent.class).polygon.contains(pos.x, pos.y)) {
          SelectionComponent selection = selectableEntity.getComponent(SelectionComponent.class);
          selection.selected = !selection.selected;
          return true;
        }
      }

      boolean actionTaken = false;
      MoveCommand command = new MoveCommand();
      command.setDestination(pos.x, pos.y);
      // No entities clicked - attempt to move entities there if relevant
      for (Entity entity : selectableEntities) {
        SelectionComponent selection = entity.getComponent(SelectionComponent.class);
        if (selection.selected) {
          command.addEntityUuid(entity.getId());
          actionTaken = true;
        }
      }

      if (command.entityUuids.size != 0) {
        commandNetwork.sendCommand(sync.synchronizeCommand(Command.moveCommand(command)));
      }

      return actionTaken;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
      return false;
    }

    @Override
    public boolean longPress(float x, float y) {
      ConsoleSystem.CONSOLE_ENABLED = !ConsoleSystem.CONSOLE_ENABLED;
      return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
      return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
      return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
      return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
      return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
      return false;
    }
  }
}
