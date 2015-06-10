package com.fivem.rts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fivem.rts.SpaceRts;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = (int)SpaceRts.SCENE_WIDTH;
    config.height = (int)SpaceRts.SCENE_HEIGHT;
    new LwjglApplication(new SpaceRts(), config);
  }
}
