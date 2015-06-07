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
import com.fivem.rts.ashley.RenderSystem;
import com.fivem.rts.ashley.SizeComponent;
import com.fivem.rts.ashley.TextureComponent;
import com.fivem.rts.ashley.TransformComponent;

public class SpaceRts extends ApplicationAdapter {

  private static final float SCENE_WIDTH = 1280;
  private static final float SCENE_HEIGHT = 720;

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

    Entity entity = new Entity();

    TextureComponent texture = new TextureComponent();
    TransformComponent transform = new TransformComponent();
    SizeComponent size = new SizeComponent();

    texture.region = new TextureRegion(new Texture(Gdx.files.internal("badlogic.jpg")));
    size.width = 200;
    size.height = 200;
    transform.position.set(SCENE_WIDTH * .5f - size.width * .5f, SCENE_HEIGHT * .5f - size.height * .5f, 0);

    entity.add(texture);
    entity.add(transform);
    entity.add(size);

    ashleyEngine.addEntity(entity);

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
