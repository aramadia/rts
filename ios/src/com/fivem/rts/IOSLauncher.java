package com.fivem.rts;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.fivem.rts.network.JsonMockNetworkManager;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public class IOSLauncher extends IOSApplication.Delegate {
  @Override
  protected IOSApplication createApplication() {
    IOSApplicationConfiguration config = new IOSApplicationConfiguration();

    GoogleServicesInterface googleServicesInterface = new GoogleServicesInterface() {
      @Override
      public void signin() {

      }

      @Override
      public void signout() {

      }

      @Override
      public void automatch() {

      }

      @Override
      public void broadcastMessage(byte[] message) {

      }

      @Override
      public void receiveMessage(String playerId, byte[] message) {

      }
    };

    return new IOSConsoleApplication(new SpaceRtsGame(googleServicesInterface, new JsonMockNetworkManager()), config);
  }

  public static void main(String[] argv) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(argv, null, IOSLauncher.class);
    pool.close();
  }
}