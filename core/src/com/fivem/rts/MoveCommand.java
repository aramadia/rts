package com.fivem.rts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.Serializable;

public class MoveCommand {

  public final Vector2 destination = new Vector2();
  public final Array<Long> entityUuids = new Array<Long>();

  public MoveCommand() {
  }

  public void setDestination(float x, float y) {
    destination.set(x, y);
  }

  public void setDestination(Vector2 destination) {
    destination.set(destination);
  }

  public void addEntityUuid(long uuid) {
    entityUuids.add(uuid);
  }
}
