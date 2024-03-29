package com.fivem.rts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fivem.rts.network.CommandNetwork;
import com.fivem.rts.network.GoogleCommandNetwork;
import com.fivem.rts.network.GoogleServicesInterface;
import com.fivem.rts.system.*;

import java.util.Random;

public class SpaceRtsGame extends ApplicationAdapter {

  // TODO this shit should be moved somewhere
  public static boolean DEBUG_MODE = false;

  // Draw fps, frame counter and status
  public static boolean DRAW_STATUS = true;

  // Global status string used for debugging only
  public static String gameStatus = "Local";

  public static final float SCENE_WIDTH = 1280;
  public static final float SCENE_HEIGHT = 720;

  // Use this constant to indicate the number of players, so we can find all references
  // easily when we need to increase the player count
  public static final int NUM_PLAYERS = 2;

  // Run the game and render loop at a fix frequency
  private static final float GAME_TICK_DURATION = 1.f/60.f;

  private OrthographicCamera camera;
  private OrthographicCamera cameraHud;

  private Viewport viewport;
  private Viewport viewportHud;

  private Engine ashleyEngine;

  private GoogleServicesInterface googleServicesInterface;
  private CommandNetwork commandNetwork;

  private SpriteBatch spriteBatch;

  // TODO Consider removing the static
  public static World world;
  public static GameSync sync;
  public static Random random = new Random(79);

  public SpaceRtsGame(GoogleServicesInterface googleServicesInterface) {
    this.googleServicesInterface = googleServicesInterface;
    this.commandNetwork = new GoogleCommandNetwork(googleServicesInterface);
  }

  @Override
  public void create() {
    googleServicesInterface.signin();

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
    sync = new GameSync();

    ashleyEngine = new Engine();

    RoomManagementSystem roomManagementSystem = new RoomManagementSystem(googleServicesInterface, commandNetwork, sync);
    CommandReadSystem commandReadSystem = new CommandReadSystem(commandNetwork, sync);
    AiSystem aiSystem = new AiSystem();
    InputSystem inputSystem = new InputSystem(camera, commandNetwork, sync);
    MovementSystem movementSystem = new MovementSystem();
    ShootingSystem shootingSystem = new ShootingSystem();
    CollisionSystem collisionSystem = new CollisionSystem();
    ParticleSystem particleSystem = new ParticleSystem();
    HashingSystem hashingSystem = new HashingSystem();
    CommandWriteSystem commandWriteSystem = new CommandWriteSystem(commandNetwork, sync);
    RenderSystem renderSystem = new RenderSystem(camera, spriteBatch);
    ConsoleSystem consoleSystem = new ConsoleSystem(viewportHud);

    // Order matters
    ashleyEngine.addSystem(roomManagementSystem);
    ashleyEngine.addSystem(commandReadSystem);
    ashleyEngine.addSystem(aiSystem);
    ashleyEngine.addSystem(inputSystem);
    ashleyEngine.addSystem(movementSystem);
    ashleyEngine.addSystem(shootingSystem);
    ashleyEngine.addSystem(collisionSystem);
    ashleyEngine.addSystem(particleSystem);
    ashleyEngine.addSystem(hashingSystem);
    ashleyEngine.addSystem(commandWriteSystem);
    ashleyEngine.addSystem(renderSystem);
    ashleyEngine.addSystem(consoleSystem);

    world.initWorld(ashleyEngine);
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    ashleyEngine.update(GAME_TICK_DURATION);
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void dispose() {
  }

}
