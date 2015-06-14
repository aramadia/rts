package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.fivem.rts.SpaceRts;
import com.fivem.rts.component.MovementComponent;
import com.fivem.rts.component.ParticleComponent;
import com.fivem.rts.component.TransformComponent;

public class InputSystem extends EntitySystem implements InputProcessor {

  private static final int ACCELERATION = 200;

  private final ComponentMapper<MovementComponent> movementMapper;

  private Engine engine;

  public InputSystem() {
    Gdx.input.setInputProcessor(this);

    movementMapper = ComponentMapper.getFor(MovementComponent.class);
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
        SpaceRts.DEBUG_MODE = !SpaceRts.DEBUG_MODE;
        return true;
    }

    return false;
  }

  private void updateAcceleration(int xAcceleration, int yAcceleration) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(Family.all(MovementComponent.class, TransformComponent.class)
            .exclude(ParticleComponent.class).get());

    for (Entity entity : entities) {
      MovementComponent movement = movementMapper.get(entity);
      movement.acceleration.set(xAcceleration, yAcceleration);
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
    return false;
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
