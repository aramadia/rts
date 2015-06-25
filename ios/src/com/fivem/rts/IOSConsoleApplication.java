package com.fivem.rts;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.fivem.rts.system.ConsoleSystem;

public class IOSConsoleApplication extends IOSApplication {
  public IOSConsoleApplication(ApplicationListener listener, IOSApplicationConfiguration config) {
    super(listener, config);
  }

  @Override
  public void log(String tag, String message) {
    super.log(tag, message);
    ConsoleSystem.addLog(tag, message);
  }

  @Override
  public void log(String tag, String message, Throwable exception) {
    super.log(tag, message, exception);
    ConsoleSystem.addLog(tag, message);
  }
}
