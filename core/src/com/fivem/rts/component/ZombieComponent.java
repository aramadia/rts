package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;

public class ZombieComponent extends Component {

  public float health = 50.0f;

  @Override
  public int hashCode() {
    int hash = 1;
    hash = 37 * hash + Float.floatToIntBits(health);

    return hash;
  }

}
