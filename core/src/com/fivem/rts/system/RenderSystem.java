package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fivem.rts.SpaceRts;
import com.fivem.rts.component.*;

public class RenderSystem extends EntitySystem {
  private final static String TAG = RenderSystem.class.getSimpleName();

  private ImmutableArray<Entity> entities;
  private ImmutableArray<Entity> selectedEntites;

  private SpriteBatch batch;
  private BitmapFont font;
  private OrthographicCamera camera;
  private ShapeRenderer shapeRenderer;

  private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
  private ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
  private ComponentMapper<TextComponent> textMapper = ComponentMapper.getFor(TextComponent.class);
  private ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
  private ComponentMapper<ParticleComponent> particleMapper = ComponentMapper.getFor(ParticleComponent.class);
  private ComponentMapper<SelectionComponent> selectedMapper = ComponentMapper.getFor(SelectionComponent.class);

  public RenderSystem(OrthographicCamera camera) {
    this.batch = new SpriteBatch();
    this.camera = camera;
    this.font = new BitmapFont();
    this.font.setColor(Color.RED);

    this.shapeRenderer = new ShapeRenderer();
  }

  @Override
  public void addedToEngine(Engine engine) {
    //noinspection unchecked
    entities = engine.getEntitiesFor(Family.all(TransformComponent.class, BoundsComponent.class).get());
    selectedEntites = engine.getEntitiesFor(
        Family.all(TransformComponent.class, BoundsComponent.class, SelectionComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    camera.update();

    batch.begin();
    batch.setProjectionMatrix(camera.combined);

    for (int i = 0; i < entities.size(); ++i) {
      Entity e = entities.get(i);

      TransformComponent transform = transformMapper.get(e);
      TextureComponent texture = textureMapper.get(e);
      TextComponent text = textMapper.get(e);
      BoundsComponent bounds = boundsMapper.get(e);

      float x = transform.position.x;
      float y = transform.position.y;

      if (texture != null) {
        // TODO probably should not be using bounds for this
        float originX = bounds.bounds.width * .5f;
        float originY = bounds.bounds.height * .5f;

        batch.draw(texture.region,
            x - originX, y - originY,
            originX, originY,
            bounds.bounds.width, bounds.bounds.height,
            transform.scale.x, transform.scale.y,
            transform.rotation);
      }

      if (text != null && SpaceRts.DEBUG_MODE) {
        font.draw(batch, text.text, x, y);
      }
    }

    if (SpaceRts.DEBUG_MODE) {
      font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, SpaceRts.SCENE_HEIGHT - 10);
    }

    batch.end();

    shapeRenderer.setProjectionMatrix(camera.combined);

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (Entity entity : entities) {
      ParticleComponent particle = particleMapper.get(entity);
      TransformComponent transform = transformMapper.get(entity);

      if (particle != null) {
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.circle(transform.position.x, transform.position.y, particle.size * 0.5f);
      }
    }
    shapeRenderer.end();

    Gdx.gl.glLineWidth(3 / camera.zoom);
    shapeRenderer.setColor(0f, 1f, 0f, 0.5f);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    for (Entity entity : selectedEntites) {
      SelectionComponent selection = selectedMapper.get(entity);
      if (selection.selected) {
        BoundsComponent bounds = boundsMapper.get(entity);
        shapeRenderer.rect(bounds.bounds.x, bounds.bounds.y, bounds.bounds.width, bounds.bounds.height);
      }
    }
    shapeRenderer.end();

    // Needs to happen outside of batch drawing
    if (SpaceRts.DEBUG_MODE) {
      shapeRenderer.setColor(1f, 0f, 0f, 0.5f);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
      for (Entity entity : entities) {
        BoundsComponent bounds = boundsMapper.get(entity);
        shapeRenderer.rect(bounds.bounds.x, bounds.bounds.y, bounds.bounds.width, bounds.bounds.height);
      }
      shapeRenderer.end();
    }

  }
}
