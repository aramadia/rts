package com.fivem.rts.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fivem.rts.SpaceRtsGame;
import com.fivem.rts.network.JsonMockNetworkManager;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = (int) SpaceRtsGame.SCENE_WIDTH;
    config.height = (int) SpaceRtsGame.SCENE_HEIGHT;
    new DesktopConsoleApplication(new SpaceRtsGame(new GoogleServicesDesktop(), new JsonMockNetworkManager()), config);
  }

}
