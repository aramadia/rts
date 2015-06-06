package com.fivem.rts.ashley;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends EntitySystem {
  private ImmutableArray<Entity> entities;

  private SpriteBatch batch;
  private OrthographicCamera camera;

  private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
  private ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
  private ComponentMapper<SizeComponent> sizeMapper = ComponentMapper.getFor(SizeComponent.class);

  public RenderSystem(OrthographicCamera camera) {
    this.batch = new SpriteBatch();
    this.camera = camera;
  }

  @Override
  public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(Family.all(
        TransformComponent.class,
        TextureComponent.class,
        SizeComponent.class
    ).get());
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
      SizeComponent size = sizeMapper.get(e);

      batch.draw(texture.region,
          transform.position.x, transform.position.y,
          size.width * .5f, size.height * .5f,
          size.width, size.height,
          transform.scale.x, transform.scale.y,
          transform.rotation);
    }

    batch.end();
  }
}
