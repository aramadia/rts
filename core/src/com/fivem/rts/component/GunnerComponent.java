package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class GunnerComponent extends Component {
  // The amount of seconds left until reloaded
  public float reload_progress = 0.0f;

  // The amount to reset once the gun shoots
  public float reload_time = 3.0f;

  public float bullet_speed = 1000.0f;

}