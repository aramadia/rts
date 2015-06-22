package com.fivem.rts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fivem.rts.SpaceRtsGame;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = (int) SpaceRtsGame.SCENE_WIDTH;
    config.height = (int) SpaceRtsGame.SCENE_HEIGHT;
    new LwjglApplication(new SpaceRtsGame(new GoogleServicesDesktop()), config);
  }
}
