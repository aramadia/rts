package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Bounding box of an object
 * Used for collision detection, sizing of drawables, selection
 */
public class BoundsComponent extends Component {
  public final Rectangle bounds = new Rectangle();
}
