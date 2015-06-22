package com.fivem.rts;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
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

    return new IOSApplication(new SpaceRtsGame(googleServicesInterface), config);
  }

  public static void main(String[] argv) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(argv, null, IOSLauncher.class);
    pool.close();
  }
}