package com.fivem.rts.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.fivem.rts.CommandManager;
import com.fivem.rts.MoveCommand;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.component.*;

public class InputSystem extends EntitySystem implements InputProcessor {

  private static final int ACCELERATION = 200;

  private final OrthographicCamera camera;
  private final CommandManager commandManager;

  private Engine engine;

  public InputSystem(OrthographicCamera camera, CommandManager commandManager
  ) {
    Gdx.input.setInputProcessor(this);

    this.camera = camera;
    this.commandManager = commandManager;
  }

  @Override
  public void addedToEngine(Engine engine) {
    this.engine = engine;
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
      case Input.Keys.ESCAPE:
        unselectAllUnits();
        return true;
    }

    return false;
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
            .exclude(ParticleComponent.class).get());

    for (Entity entity : entities) {
      entity.getComponent(MovementComponent.class).acceleration.set(xAcceleration, yAcceleration);
    }
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector3 pos = camera.unproject(new Vector3(screenX, screenY, 0));

    ImmutableArray<Entity> selectableEntities =
        engine.getEntitiesFor(Family.all(SelectionComponent.class, BoundsComponent.class).get());

    for (Entity selectableEntity : selectableEntities) {
      if (selectableEntity.getComponent(BoundsComponent.class).bounds.contains(pos.x, pos.y)) {
        SelectionComponent selection = selectableEntity.getComponent(SelectionComponent.class);
        selection.selected = !selection.selected;
        return true;
      }
    }

    boolean actionTaken = false;
    MoveCommand moveCommand = new MoveCommand(pos.x, pos.y);
    // No entities clicked - attempt to move entities there if relevant
    for (Entity entity : selectableEntities) {
      SelectionComponent selection = entity.getComponent(SelectionComponent.class);
      if (selection.selected) {
        moveCommand.addEntityUuid(entity.getId());
        actionTaken = true;
      }
    }

    if (moveCommand.entityUuids.size != 0) {
      commandManager.addCommand(moveCommand);
    }

    return actionTaken;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
