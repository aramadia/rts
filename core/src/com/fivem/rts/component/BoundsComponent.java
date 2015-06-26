package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Bounding box of an object
 * Used for collision detection, sizing of drawables, selection
 */
public class BoundsComponent extends Component {
  public final Polygon polygon = new Polygon();
  public final Rectangle rect = new Rectangle();

  public void setBoundsFromRect(float x, float y, float width, float height) {
    polygon.setVertices(new float[]{0, 0, width, 0, width, height, 0, height});
    polygon.setOrigin(width * 0.5f, height * 0.5f);
    polygon.translate(x - width * 0.5f, y - height * 0.5f);

    // TODO move this somewhere else
    rect.set(x, y, width, height);
  }
}
