package com.fivem.rts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fivem.rts.component.*;
import com.fivem.rts.network.NetworkManager;
import com.fivem.rts.system.*;

import java.util.Random;

public class SpaceRtsGame extends ApplicationAdapter {

  // TODO this shit should be moved somewhere
  public static boolean DEBUG_MODE = false;

  public static final float SCENE_WIDTH = 1280;
  public static final float SCENE_HEIGHT = 720;

  private OrthographicCamera camera;
  private OrthographicCamera cameraHud;

  private Viewport viewport;
  private Viewport viewportHud;

  private Engine ashleyEngine;

  static GoogleServicesInterface googleServicesInterface;
  private CommandManager commandManager;
  private NetworkManager networkManager;
  public static Random random;
  private SpriteBatch spriteBatch;
  private World world;

  public SpaceRtsGame(GoogleServicesInterface googleServicesInterface, NetworkManager networkManager){
    this.googleServicesInterface = googleServicesInterface;
    this.networkManager = networkManager;
    this.random = new Random(79);
  }

  @Override
  public void create() {
    this.googleServicesInterface.signin();

    camera = new OrthographicCamera();
    cameraHud = new OrthographicCamera();
    viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
    viewportHud = new FillViewport(SCENE_WIDTH, SCENE_HEIGHT, cameraHud);
    world = new World();

    // Center camera
    viewport.getCamera().position.set(
        viewport.getCamera().position.x + SCENE_WIDTH * 0.5f,
        viewport.getCamera().position.y + SCENE_HEIGHT * 0.5f,
        0
    );
    viewport.getCamera().update();
    viewport.update((int) SCENE_WIDTH, (int) SCENE_HEIGHT);
    camera.update();

    spriteBatch = new SpriteBatch();

    ashleyEngine = new Engine();
    commandManager = new CommandManager(this.networkManager);

    CommandReadSystem commandReadSystem = new CommandReadSystem(commandManager);
    InputSystem inputSystem = new InputSystem(camera, commandManager);
    MovementSystem movementSystem = new MovementSystem();
    ShootingSystem shootingSystem = new ShootingSystem();
    CollisionSystem collisionSystem = new CollisionSystem();
    ParticleSystem particleSystem = new ParticleSystem();
    RenderSystem renderSystem = new RenderSystem(camera, spriteBatch);
    ConsoleSystem consoleSystem = new ConsoleSystem(viewportHud);
    CommandWriteSystem commandWriteSystem = new CommandWriteSystem(commandManager);

    // Order matters
    ashleyEngine.addSystem(commandReadSystem);
    ashleyEngine.addSystem(inputSystem);
    ashleyEngine.addSystem(movementSystem);
    ashleyEngine.addSystem(shootingSystem);
    ashleyEngine.addSystem(collisionSystem);
    ashleyEngine.addSystem(particleSystem);
    ashleyEngine.addSystem(renderSystem);
    ashleyEngine.addSystem(consoleSystem);
    ashleyEngine.addSystem(commandWriteSystem);

    world.initWorld(ashleyEngine);
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
