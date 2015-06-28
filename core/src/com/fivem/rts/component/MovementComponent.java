package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent extends Component {
  public Vector2 velocity = new Vector2();
  public Vector2 acceleration = new Vector2();

  @Override
  public int hashCode() {
    int hash = 1;
    hash = 37 * hash + velocity.hashCode();
    hash = 37 * hash + acceleration.hashCode();

    return hash;
  }
}
