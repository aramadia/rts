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
        style = new Label.LabelStyle(new BitmapFont(), color);
      }

      return style;
    }
  }

  private final Stage stage;
  private final Label.LabelStyle titleStyle;
  private BitmapFont font;
  private ScrollPane scrollPane;
  private Table table;

  public ConsoleSystem(Viewport viewport) {
    this.stage = new Stage(viewport);
    this.font = new BitmapFont();
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
      table.add(new Label(logEntry.message, logEntry.logLevel.style)).expandX().fillX().top().left().padLeft(4).row();
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
