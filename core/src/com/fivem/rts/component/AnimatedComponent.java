package com.fivem.rts.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Gives an entity a spritesheet style animation
 * Requires TransformComponent, BoundsComponent
 */
public class AnimatedComponent extends Component {
  // Full texture sheet
  public Texture sheet;
  // 1D array of each keyframe
  public TextureRegion[] frames;
  // Animation state machine
  public Animation animation;
  // Current frame selected to render
  public TextureRegion currentFrame;
  // The accumulated time for this entity (controls animation)
  public float animationTime;
}
