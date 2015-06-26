package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Bounding box of an object
 * Used for collision detection, sizing of drawables, selection
 */
public class BoundsComponent extends Component {
  public final Polygon newBounds = new Polygon();
  public final Rectangle bounds = new Rectangle();

  public void setBoundsFromRect(float x, float y, float width, float height) {
    newBounds.setVertices(new float[]{0, 0, width, 0, width, height, 0, height});
    newBounds.setOrigin(width * 0.5f, height * 0.5f);
    newBounds.translate(x - width * 0.5f, y - height * 0.5f);
  }
}
