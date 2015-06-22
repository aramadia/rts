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
import com.fivem.rts.system.*;

public class SpaceRtsGame extends ApplicationAdapter {

  public static boolean DEBUG_MODE = false;

  public static final float SCENE_WIDTH = 1280;
  public static final float SCENE_HEIGHT = 720;

  private OrthographicCamera camera;
  private Viewport viewport;

  private Engine ashleyEngine;

  static GoogleServicesInterface googleServicesInterface;

  public SpaceRtsGame(GoogleServicesInterface googleServicesInterface){
    this.googleServicesInterface = googleServicesInterface;
  }

  @Override
  public void create() {
    this.googleServicesInterface.signin();

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

    InputSystem inputSystem = new InputSystem(camera);
    MovementSystem movementSystem = new MovementSystem();
    BoundsSystem boundsSystem = new BoundsSystem();
    CollisionSystem collisionSystem = new CollisionSystem();
    ShootingSystem shootingSystem = new ShootingSystem();
    RenderSystem renderSystem = new RenderSystem(camera);

    // Order matters
    ashleyEngine.addSystem(inputSystem);
    ashleyEngine.addSystem(movementSystem);
    ashleyEngine.addSystem(boundsSystem);
    ashleyEngine.addSystem(shootingSystem);
    ashleyEngine.addSystem(collisionSystem);
    ashleyEngine.addSystem(renderSystem);

    for (int i = 0; i < 2; i++) {
      Entity entity = createSmileyEntity(i);
      ashleyEngine.addEntity(entity);
    }

    shootingSystem.addedToEngine(ashleyEngine);
    collisionSystem.addedToEngine(ashleyEngine);
    renderSystem.addedToEngine(ashleyEngine);
  }

  private Entity createSmileyEntity(int i) {
    Entity entity = new Entity();

    TextureComponent texture = new TextureComponent();
    TransformComponent transform = new TransformComponent();
    BoundsComponent bounds = new BoundsComponent();
    MovementComponent movement = new MovementComponent();
    TextComponent text = new TextComponent();
    GunnerComponent gunner = new GunnerComponent();
    SelectionComponent selection = new SelectionComponent();

    int width = 100;
    int height = 100;

    text.text = "Entity" + i;

    texture.region = new TextureRegion(new Texture(Gdx.files.internal("badlogic.jpg")));
    transform.position.set(SCENE_WIDTH * (float)Math.random() - bounds.bounds.width * .4f,
            SCENE_HEIGHT * (float)Math.random() - bounds.bounds.height * .4f, 0);
    bounds.bounds.set(transform.position.x - width * 0.5f, transform.position.y * 0.5f, width, height);
    movement.velocity.set(10, 10);
    movement.acceleration.set(30, 30);

    gunner.reload_progress = (float)Math.random() * gunner.reload_time;

    entity.add(texture);
    entity.add(transform);
    entity.add(bounds);
    entity.add(movement);
    entity.add(text);
    entity.add(gunner);
    entity.add(selection);

    return entity;
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