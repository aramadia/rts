package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;

public class BulletComponent extends Component {

  public float size = 12.0f;

  // How much longer this particle exists.
  public float lifespan = 10.0f;

  // How much damage the bullet causes
  public float damage = 20.0f;

}
