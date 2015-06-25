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
    BoundsSystem boundsSystem = new BoundsSystem();
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
    ashleyEngine.addSystem(boundsSystem);
    ashleyEngine.addSystem(shootingSystem);
    ashleyEngine.addSystem(collisionSystem);
    ashleyEngine.addSystem(particleSystem);
    ashleyEngine.addSystem(renderSystem);
    ashleyEngine.addSystem(consoleSystem);
    ashleyEngine.addSystem(commandWriteSystem);


    Entity entity = createSmileyEntity(0);
    ashleyEngine.addEntity(entity);

    for (int i = 0; i < 50; i++) {



      Entity zombie = createZombie(i);
      ashleyEngine.addEntity(zombie);
    }
  }

  private Entity createZombie(int i) {
    Entity entity = new Entity();

    AnimatedComponent animation = new AnimatedComponent();
    TransformComponent transform = new TransformComponent();
    BoundsComponent bounds = new BoundsComponent();
    MovementComponent movement = new MovementComponent();
    TextComponent text = new TextComponent();
    SelectionComponent selection = new SelectionComponent();

    int width = 100;
    int height = 100;

    text.text = "Zombie" + i;

    animation.sheet = new Texture(Gdx.files.internal("zombiewalk.png")); // #9
    TextureRegion[][] tmp = TextureRegion.split(animation.sheet, animation.sheet.getWidth()/8,
        animation.sheet.getHeight()/1);              // #10
    animation.frames = new TextureRegion[8 * 1];
    int index = 0;
    for (int y = 0; y < 1; y++) {
      for (int x = 0; x < 8; x++) {
        animation.frames[index++] = tmp[y][x];
      }
    }
    animation.animation = new Animation(0.1f, animation.frames);
    transform.position.set(SCENE_WIDTH * random.nextFloat() - bounds.bounds.width * .4f,
        SCENE_HEIGHT * random.nextFloat() - bounds.bounds.height * .4f, 0);
    transform.scale.set(0.5f, 0.5f);
    bounds.bounds.set(transform.position.x - width * transform.scale.x * 0.5f, transform.position.y * 0.5f,
        width  * transform.scale.x, height  * transform.scale.y);
    movement.velocity.set(100, 0).setAngleRad(MathUtils.PI2 * random.nextFloat());

    entity.add(animation);
    entity.add(transform);
    entity.add(bounds);
    entity.add(movement);
    entity.add(text);
    entity.add(selection);

    return entity;
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
    transform.position.set(SCENE_WIDTH * random.nextFloat() - bounds.bounds.width * .4f,
            SCENE_HEIGHT * random.nextFloat() - bounds.bounds.height * .4f, 0);
    bounds.bounds.set(transform.position.x - width * 0.5f, transform.position.y * 0.5f, width, height);
    movement.velocity.set(10, 10);
    movement.acceleration.set(30, 30);

    gunner.reload_progress = random.nextFloat() * gunner.reload_time;

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
