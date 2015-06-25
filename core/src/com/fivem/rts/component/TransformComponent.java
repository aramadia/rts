package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * 2D Pose of an object
 * Translation, scaling and rotation
 */
public class TransformComponent extends Component {
  public final Vector3 position = new Vector3();
  public final Vector2 scale = new Vector2(1.0f, 1.0f);
  // the angle of counter clockwise rotation of the rectangle around originX/originY
  public float rotation = 0.0f;
}
