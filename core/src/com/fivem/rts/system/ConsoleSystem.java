package com.fivem.rts.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fivem.rts.SpaceRtsGame;

public class ConsoleSystem extends EntitySystem {

  private final Stage stage;
  private BitmapFont font;
  private String text;
  private ScrollPane scrollPane;
  private Array<Label> labels;
  private Table table;

  public ConsoleSystem(Viewport viewport) {
    this.stage = new Stage(viewport);
    this.font = new BitmapFont();
    this.font.setColor(Color.RED);

    text = "Console \n";
    for (int i = 0; i < 250; i++) {
      text += i + "line \n";
    }

    labels = new Array<Label>();
    for (int i = 0; i < 250; i++) {
      labels.add(new Label(i + "line \n", new Label.LabelStyle(font, Color.GREEN)));
    }

    table = new Table();
    for (Label label : labels) {
      table.add(label).expandX().fillX().top().left().padLeft(4).row();
    }

    ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();

    scrollPane = new ScrollPane(table, sps);
    scrollPane.setWidth(stage.getWidth() - 40);
    scrollPane.setHeight(stage.getHeight() - 40);
    scrollPane.setPosition(20, 20);
    scrollPane.validate();
    scrollPane.setScrollPercentY(1);

    stage.addActor(scrollPane);
  }

  @Override
  public void update(float deltaTime) {
    if (!SpaceRtsGame.CONSOLE_ENABLED) {
      return;
    }

    stage.act(deltaTime);
    stage.draw();
  }
}
