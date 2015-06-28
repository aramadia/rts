package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Add this component to give an entity the ability to shoot bullets
 * Requires: TransformComponent
 */
public class GunnerComponent extends Component {
  // The amount of seconds left until reloaded
  public float reloadProgress = 0.0f;

  // The amount to reset once the gun shoots
  public float reloadTime = .2f;

  public float bulletSpeed = 1400.0f;

}
