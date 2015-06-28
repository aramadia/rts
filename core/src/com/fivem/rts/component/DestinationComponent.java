package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class DestinationComponent extends Component {
  public Vector2 position = new Vector2();

  @Override
  public int hashCode() {
    return position.hashCode();
  }
}
