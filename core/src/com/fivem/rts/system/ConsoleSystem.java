package com.fivem.rts.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
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

  private static final String TAG = ConsoleSystem.class.getSimpleName();

  public static boolean CONSOLE_ENABLED = false;

  private static Array<LogEntry> logEntries = new Array<LogEntry>();

  enum LogLevel {
    DEBUG(Color.GREEN),
    LOG(Color.WHITE),
    ERROR(Color.RED);

    private final Color color;
    private Label.LabelStyle style;

    LogLevel(Color color) {
      this.color = color;
    }

    public Label.LabelStyle getStyle() {
      if (style == null) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(getFontScale());
        style = new Label.LabelStyle(font, color);
      }

      return style;
    }
  }

  private static float getFontScale() {
    float density = Gdx.app.getGraphics().getDensity();
    if (density < 1) {
      density = 1;
    }

    Gdx.app.log(TAG, "Font density " + density);
    return density;
  }

  private final Stage stage;
  private final Label.LabelStyle titleStyle;
  private BitmapFont font;
  private ScrollPane scrollPane;
  private Table table;

  public ConsoleSystem(Viewport viewport) {
    this.stage = new Stage(viewport);
    this.font = new BitmapFont();
    this.font.getData().setScale(getFontScale());
    this.font.setColor(Color.RED);

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

    // TODO print exception
    table.clear();
    table.add(new Label("CONSOLE", titleStyle)).expandX().fillX().top().left().padLeft(4).row();
    for (LogEntry logEntry : logEntries) {
      table.add(new Label(logEntry.message, logEntry.logLevel.getStyle())).expandX().fillX().top().left().padLeft(4).row();
    }

    scrollPane.validate();
    scrollPane.setScrollPercentY(1);

    stage.act(deltaTime);
    stage.draw();
  }

  public static void debug(String tag, String message) {
    debug(tag, message, null);
  }

  public static void debug(String tag, String message, Throwable exception) {
    logEntries.add(new LogEntry(LogLevel.DEBUG, tag, message, exception));
  }

  public static void log(String tag, String message) {
    log(tag, message, null);
  }

  public static void log(String tag, String message, Throwable exception) {
    logEntries.add(new LogEntry(LogLevel.LOG, tag, message, exception));
  }

  public static void error(String tag, String message) {
    error(tag, message, null);
  }

  public static void error(String tag, String message, Throwable exception) {
    // Display error status
    SpaceRtsGame.gameStatus = "ERROR: " + tag + ": " +  message;
    logEntries.add(new LogEntry(LogLevel.ERROR, tag, message, exception));
  }

  private static class LogEntry {
    public LogLevel logLevel;
    public String tag;
    public String message;
    public Throwable exception;

    public LogEntry(LogLevel logLevel, String tag, String message) {
      this.logLevel = logLevel;
      this.tag = tag;
      this.message = message;
    }

    public LogEntry(LogLevel logLevel, String tag, String message, Throwable exception) {
      this.logLevel = logLevel;
      this.tag = tag;
      this.message = message;
      this.exception = exception;
    }
  }

}
