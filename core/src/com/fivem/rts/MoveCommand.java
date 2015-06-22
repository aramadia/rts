package com.fivem.rts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.Serializable;

public class MoveCommand implements Serializable {

  public final Vector2 destination;
  public final Array<Long> entityUuids = new Array<Long>();

  public MoveCommand(float x, float y) {
    this.destination = new Vector2(x, y);
  }

  public MoveCommand(Vector2 destination) {
    this.destination = destination;
  }

  public void addEntityUuid(long uuid) {
    entityUuids.add(uuid);
  }

}
