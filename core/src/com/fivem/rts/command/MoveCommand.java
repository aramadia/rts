package com.fivem.rts.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class MoveCommand extends BaseCommand {

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

  @Override
  public String toString() {
    return "Ships: " + entityUuids.toString() + " move to " + destination.toString();
  }
}

