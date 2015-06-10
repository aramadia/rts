package com.fivem.rts.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fivem.rts.component.SizeComponent;
import com.fivem.rts.component.TextComponent;
import com.fivem.rts.component.TextureComponent;
import com.fivem.rts.component.TransformComponent;

public class RenderSystem extends EntitySystem {

  private ImmutableArray<Entity> entities;

  private SpriteBatch batch;
  private BitmapFont font;
  private OrthographicCamera camera;

  private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
  private ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
  private ComponentMapper<TextComponent> textMapper = ComponentMapper.getFor(TextComponent.class);
  private ComponentMapper<SizeComponent> sizeMapper = ComponentMapper.getFor(SizeComponent.class);


  public RenderSystem(OrthographicCamera camera) {
    this.batch = new SpriteBatch();
    this.camera = camera;
    this.font = new BitmapFont();
    this.font.setColor(Color.RED);
  }

  @Override
  public void addedToEngine(Engine engine) {
      //noinspection unchecked
      entities = engine.getEntitiesFor(Family.all(
            TransformComponent.class,
            SizeComponent.class)
                    .one(TextureComponent.class, TextComponent.class)
      .get());
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
      SizeComponent size = sizeMapper.get(e);

      if (texture != null) {
        batch.draw(texture.region,
            transform.position.x, transform.position.y,
            size.width * .5f, size.height * .5f,
            size.width, size.height,
            transform.scale.x, transform.scale.y,
            transform.rotation);
      }

      if (text != null) {
        font.draw(batch, text.text, transform.position.x, transform.position.y);
      }
    }

    batch.end();
  }
}
