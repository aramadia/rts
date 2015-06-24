package com.fivem.rts.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.network.JsonMockNetworkManager;
import com.fivem.rts.system.ConsoleSystem;

import java.awt.*;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = (int) SpaceRtsGame.SCENE_WIDTH;
    config.height = (int) SpaceRtsGame.SCENE_HEIGHT;
    new ConsoleApplication(new SpaceRtsGame(new GoogleServicesDesktop(), new JsonMockNetworkManager()), config);
  }

  private static class ConsoleApplication extends LwjglApplication {

    public ConsoleApplication(ApplicationListener listener, String title, int width, int height) {
      super(listener, title, width, height);
    }

    public ConsoleApplication(ApplicationListener listener) {
      super(listener);
    }

    public ConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
      super(listener, config);
    }

    public ConsoleApplication(ApplicationListener listener, Canvas canvas) {
      super(listener, canvas);
    }

    public ConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config, Canvas canvas) {
      super(listener, config, canvas);
    }

    public ConsoleApplication(ApplicationListener listener, LwjglApplicationConfiguration config, LwjglGraphics graphics) {
      super(listener, config, graphics);
    }

    @Override
    public void log(String tag, String message) {
      super.log(tag, message);
      ConsoleSystem.addLog(message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
      super.log(tag, message, exception);
      ConsoleSystem.addLog(message);
    }
  }
}
