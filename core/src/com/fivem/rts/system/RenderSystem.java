package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.component.*;

public class RenderSystem extends EntitySystem {
  private final static String TAG = RenderSystem.class.getSimpleName();

  private ImmutableArray<Entity> entities;
  private ImmutableArray<Entity> selectedEntites;
  private ImmutableArray<Entity> particleEntities;

  private Engine engine;
  private SpriteBatch batch;
  private BitmapFont font;
  private OrthographicCamera camera;
  private ShapeRenderer shapeRenderer;

  private ComponentMapper<AnimatedComponent> animatedComponent = ComponentMapper.getFor(AnimatedComponent.class);
  private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
  private ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
  private ComponentMapper<TextComponent> textMapper = ComponentMapper.getFor(TextComponent.class);
  private ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
  private ComponentMapper<BulletComponent> bulletMapper = ComponentMapper.getFor(BulletComponent.class);
  private ComponentMapper<SelectionComponent> selectedMapper = ComponentMapper.getFor(SelectionComponent.class);
  private ComponentMapper<DestinationComponent> destinationMapper = ComponentMapper.getFor(DestinationComponent.class);
  private ComponentMapper<ParticleComponent> particleMapper = ComponentMapper.getFor(ParticleComponent.class);

  public RenderSystem(OrthographicCamera camera, SpriteBatch spriteBatch) {
    this.batch = spriteBatch;
    this.camera = camera;
    this.font = new BitmapFont();
    this.font.setColor(Color.RED);

    this.shapeRenderer = new ShapeRenderer();

  }

  @Override
  public void addedToEngine(Engine engine) {
    //noinspection unchecked
    this.engine = engine;
    entities = engine.getEntitiesFor(Family.all(TransformComponent.class, BoundsComponent.class).get());
    selectedEntites = engine.getEntitiesFor(

        Family.all(TransformComponent.class, BoundsComponent.class, SelectionComponent.class).get());
    particleEntities = engine.getEntitiesFor(Family.all(ParticleComponent.class).get());

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
      AnimatedComponent animation = animatedComponent.get(e);

      float x = transform.position.x;
      float y = transform.position.y;

      if (texture != null) {
        // TODO probably should not be using rect for this
        float originX = bounds.rect.width * .5f;
        float originY = bounds.rect.height * .5f;

        batch.draw(texture.region,
            x - originX, y - originY,
            originX, originY,
            bounds.rect.width, bounds.rect.height,
            transform.scale.x, transform.scale.y,
            transform.rotation);
      }

      if (animation != null) {
        // TODO probably should not be using rect for this
        float originX = bounds.rect.width * .5f;
        float originY = bounds.rect.height * .5f;

        animation.animationTime += deltaTime;
        animation.currentFrame = animation.animation.getKeyFrame(animation.animationTime, true);


        batch.draw(animation.currentFrame,
            x - originX, y - originY,
            originX, originY,
            bounds.rect.width, bounds.rect.height,
            transform.scale.x, transform.scale.y,
            transform.rotation);
      }

      if (text != null && SpaceRtsGame.DEBUG_MODE) {
        font.draw(batch, text.text, x, y);
      }

    }

    for (Entity particleEntity : particleEntities) {
      ParticleEffect particleEffect = particleMapper.get(particleEntity).particleEffect;
      particleEffect.draw(batch, deltaTime);

      if (particleEffect.isComplete()) {
        engine.removeEntity(particleEntity);
      }
    }

    if (SpaceRtsGame.DRAW_STATUS) {
      // Need the extra space at the end so it doesn't get cut off.

      font.draw(batch,  Gdx.graphics.getFramesPerSecond()+ " FPS ", SpaceRtsGame.SCENE_WIDTH - 80, SpaceRtsGame.SCENE_HEIGHT - 10);
      font.draw(batch, engine.getEntities().size() + " entities " , SpaceRtsGame.SCENE_WIDTH - 80, SpaceRtsGame.SCENE_HEIGHT - 30);
      font.draw(batch, "Status: " + SpaceRtsGame.gameStatus + " ",  SpaceRtsGame.SCENE_WIDTH/2 - 40, SpaceRtsGame.SCENE_HEIGHT - 10);
      font.draw(batch, "Frame: " + SpaceRtsGame.sync.getFrame() + " ", SpaceRtsGame.SCENE_WIDTH/2 - 40, SpaceRtsGame.SCENE_HEIGHT - 30);
    }

    batch.end();

    shapeRenderer.setProjectionMatrix(camera.combined);

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (Entity entity : entities) {
      BulletComponent particle = bulletMapper.get(entity);
      TransformComponent transform = transformMapper.get(entity);
      DestinationComponent destination = destinationMapper.get(entity);

      if (destination != null) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(destination.position.x, destination.position.y, 5f);
      }

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
        drawPolygon(bounds.polygon);
      }
    }
    shapeRenderer.end();

    // Needs to happen outside of batch drawing
    if (SpaceRtsGame.DEBUG_MODE) {
      shapeRenderer.setColor(1f, 0f, 0f, 0.5f);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
      for (Entity entity : entities) {
        BoundsComponent bounds = boundsMapper.get(entity);
        drawPolygon(bounds.polygon);
      }
      shapeRenderer.end();
    }

  }

  private void drawPolygon(Polygon polygon) {
    float[] vertices = polygon.getTransformedVertices();
    float x1, x2, y1, y2;
    if (vertices.length > 2) {
      final float firstX = vertices[0];
      final float firstY = vertices[1];
      for (int i = 0; i < vertices.length; i += 2) {
        x1 = vertices[i];
        y1 = vertices[i + 1];

        if (i + 2 >= vertices.length) {
          x2 = firstX;
          y2 = firstY;
        } else {
          x2 = vertices[i + 2];
          y2 = vertices[i + 3];
        }

        shapeRenderer.line(x1, y1, x2, y2);
      }
    }
  }
}
