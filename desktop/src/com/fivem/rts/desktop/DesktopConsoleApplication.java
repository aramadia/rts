package com.fivem.rts.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.fivem.rts.system.ConsoleSystem;

import java.awt.*;

public class DesktopConsoleApplication extends LwjglApplication {

  public DesktopConsoleApplication(ApplicationListener listener, String title, int width, int height) {
    super(listener, title, width, height);
  }

  public DesktopConsoleApplication(ApplicationListener listener) {
    super(listener);
  }

  public DesktopConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
    super(listener, config);
  }

  public DesktopConsoleApplication(ApplicationListener listener, Canvas canvas) {
    super(listener, canvas);
  }

  public DesktopConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config, Canvas canvas) {
    super(listener, config, canvas);
  }

  public DesktopConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config, LwjglGraphics graphics) {
    super(listener, config, graphics);
  }

  @Override
  public void debug(String tag, String message) {
    super.debug(tag, message);
    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.addLog(tag, message);
    }
  }

  @Override
  public void debug(String tag, String message, Throwable exception) {
    super.debug(tag, message, exception);
    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.addLog(tag, message, exception);
    }
  }

  @Override
  public void log(String tag, String message) {
    super.log(tag, message);
    if (logLevel >= LOG_INFO) {
      ConsoleSystem.addLog(tag, message);
    }
  }

  @Override
  public void log(String tag, String message, Throwable exception) {
    super.log(tag, message, exception);
    if (logLevel >= LOG_INFO) {
      ConsoleSystem.addLog(tag, message, exception);
    }
  }

  @Override
  public void error(String tag, String message) {
    super.error(tag, message);
    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.addLog(tag, message);
    }
  }

  @Override
  public void error(String tag, String message, Throwable exception) {
    super.error(tag, message, exception);
    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.addLog(tag, message, exception);
    }
  }
}
