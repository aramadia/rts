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

public class ConsoleSystem extends EntitySystem {

  public static boolean CONSOLE_ENABLED = false;

  private static Array<String> logs = new Array<String>();

  private final Stage stage;
  private final Label.LabelStyle style;
  private final Label.LabelStyle titleStyle;
  private BitmapFont font;
  private ScrollPane scrollPane;
  private Table table;

  public ConsoleSystem(Viewport viewport) {
    this.stage = new Stage(viewport);
    this.font = new BitmapFont();
    this.font.setColor(Color.RED);

    style = new Label.LabelStyle(font, Color.GREEN);
    titleStyle = new Label.LabelStyle(font, Color.BLUE);
    table = new Table();

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
    if (!CONSOLE_ENABLED) {
      return;
    }

    table.clear();
    table.add(new Label("CONSOLE", titleStyle)).expandX().fillX().top().left().padLeft(4).row();
    for (String message : logs) {
      table.add(new Label(message, style)).expandX().fillX().top().left().padLeft(4).row();
    }

    scrollPane.validate();
    scrollPane.setScrollPercentY(1);

    stage.act(deltaTime);
    stage.draw();
  }

  public static void addLog(String tag, String message) {
    logs.add(message);
  }

  public static void addLog(String tag, String message, Throwable exception) {
    logs.add(message);
    // TODO print exception
  }

}
