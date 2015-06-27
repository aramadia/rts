package com.fivem.rts;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.fivem.rts.network.GoogleServicesInterface;
import com.fivem.rts.network.JsonMockNetworkManager;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import java.util.ArrayList;
import java.util.List;

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
      public GoogleRoom connected() {
        return null;
      }

      @Override
      public void broadcastMessage(byte[] message) {

      }

      @Override
      public List<Message> receiveMessages() {
        return new ArrayList<Message>();
      }


    };

    return new IOSConsoleApplication(new SpaceRtsGame(googleServicesInterface), config);
  }

  public static void main(String[] argv) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(argv, null, IOSLauncher.class);
    pool.close();
  }
}