package com.fivem.rts.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.fivem.rts.SpaceRts;

/**
 * Created by Joshua Lauer
 */
public class InputSystem extends EntitySystem implements InputProcessor {

  public InputSystem() {
    Gdx.input.setInputProcessor(this);
  }

  @Override
  public boolean keyDown(int keycode) {
    if (Input.Keys.Z == keycode) {
      SpaceRts.DEBUG_MODE = !SpaceRts.DEBUG_MODE;
      return true;
    }

    return false;
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
