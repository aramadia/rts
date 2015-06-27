package com.fivem.rts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.fivem.rts.component.*;

/**
 * Contains helpers to initialize the world.
 * Created by Daniel on 6/25/2015.
 */
public class World {
  public void initWorld(Engine engine) {
    for (int i = 0; i < 3; i++) {
      Entity entity = createSmileyEntity(i);
      engine.addEntity(entity);
    }

    for (int i = 0; i < 50; i++) {
      Entity zombie = createZombie(i);
      engine.addEntity(zombie);
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
    ZombieComponent zombie = new ZombieComponent();

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
    transform.position.set(SpaceRtsGame.SCENE_WIDTH * SpaceRtsGame.random.nextFloat() - width * .4f,
        SpaceRtsGame.SCENE_HEIGHT * SpaceRtsGame.random.nextFloat() - height * .4f, 0);
    transform.scale.set(0.5f, 0.5f);
    bounds.setBoundsFromRect(transform.position.x, transform.position.y, width, height);
    movement.velocity.set(100, 0).setAngleRad(MathUtils.PI2 * SpaceRtsGame.random.nextFloat());

    entity.add(animation);
    entity.add(transform);
    entity.add(bounds);
    entity.add(movement);
    entity.add(text);
    entity.add(selection);
    entity.add(zombie);

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
    transform.position.set(SpaceRtsGame.SCENE_WIDTH * SpaceRtsGame.random.nextFloat() - width * .4f,
        SpaceRtsGame.SCENE_HEIGHT * SpaceRtsGame.random.nextFloat() - height * .4f, 0);
    bounds.setBoundsFromRect(transform.position.x, transform.position.y, width, height);
    movement.velocity.set(10, 10);
    movement.acceleration.set(30, 30);

    gunner.reload_progress = SpaceRtsGame.random.nextFloat() * gunner.reload_time;

    entity.add(texture);
    entity.add(transform);
    entity.add(bounds);
    entity.add(movement);
    entity.add(text);
    entity.add(gunner);
    entity.add(selection);

    return entity;
  }
}
