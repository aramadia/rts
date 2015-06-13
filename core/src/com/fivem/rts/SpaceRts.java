package com.fivem.rts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fivem.rts.component.*;
import com.fivem.rts.system.BoundsSystem;
import com.fivem.rts.system.MovementSystem;
import com.fivem.rts.system.RenderSystem;

public class SpaceRts extends ApplicationAdapter {

  public static final float SCENE_WIDTH = 1280;
  public static final float SCENE_HEIGHT = 720;

  private OrthographicCamera camera;
  private Viewport viewport;

  private Engine ashleyEngine;

  @Override
  public void create() {
    camera = new OrthographicCamera();
    viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);

    // Center camera
    viewport.getCamera().position.set(
        viewport.getCamera().position.x + SCENE_WIDTH * 0.5f,
        viewport.getCamera().position.y + SCENE_HEIGHT * 0.5f,
        0
    );
    viewport.getCamera().update();
    viewport.update((int) SCENE_WIDTH, (int) SCENE_HEIGHT);
    camera.update();

    ashleyEngine = new Engine();
    RenderSystem renderSystem = new RenderSystem(camera);
    ashleyEngine.addSystem(renderSystem);
    BoundsSystem boundsSystem = new BoundsSystem();
    ashleyEngine.addSystem(boundsSystem);
    MovementSystem movementSystem = new MovementSystem();
    ashleyEngine.addSystem(movementSystem);

    for (int i = 0; i < 15; i++) {
      Entity entity = new Entity();

      TextureComponent texture = new TextureComponent();
      TransformComponent transform = new TransformComponent();
      SizeComponent size = new SizeComponent();
      MovementComponent movement = new MovementComponent();
      TextComponent text = new TextComponent();
      BoundsComponent boundsComponent = new BoundsComponent();

      int width = 100;
      int height = 100;
      boundsComponent.bounds.setWidth(width);
      boundsComponent.bounds.setHeight(height);

      text.text = "Entity" + i;

      texture.region = new TextureRegion(new Texture(Gdx.files.internal("badlogic.jpg")));
      size.width = width;
      size.height = height;
      transform.position.set(SCENE_WIDTH * (float)Math.random() - size.width * .5f,
              SCENE_HEIGHT * (float)Math.random() - size.height * .5f, 0);
      movement.velocity.set(10, 10);
      movement.acceleration.set(30, 30);

      entity.add(texture);
      entity.add(transform);
      entity.add(size);
      entity.add(movement);
      entity.add(text);

      ashleyEngine.addEntity(entity);
    }

    renderSystem.addedToEngine(ashleyEngine);
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    ashleyEngine.update(Gdx.graphics.getDeltaTime());
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void dispose() {
  }


}
