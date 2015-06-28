package com.fivem.rts;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.fivem.rts.system.ConsoleSystem;

public class IOSConsoleApplication extends IOSApplication {
  public IOSConsoleApplication(ApplicationListener listener, IOSApplicationConfiguration config) {
    super(listener, config);
  }

  // TODO iOS made the logLevel package scope so we cannot access it
  @Override
  public void debug(String tag, String message) {
    super.debug(tag, message);
//    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.addLog(tag, message);
//    }
  }

  @Override
  public void debug(String tag, String message, Throwable exception) {
    super.debug(tag, message, exception);
//    if (logLevel >= LOG_DEBUG) {
      ConsoleSystem.addLog(tag, message, exception);
//    }
  }

  @Override
  public void log(String tag, String message) {
    super.log(tag, message);
//    if (logLevel >= LOG_INFO) {
      ConsoleSystem.addLog(tag, message);
//    }
  }

  @Override
  public void log(String tag, String message, Throwable exception) {
    super.log(tag, message, exception);
//    if (logLevel >= LOG_INFO) {
      ConsoleSystem.addLog(tag, message, exception);
//    }
  }

  @Override
  public void error(String tag, String message) {
    super.error(tag, message);
//    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.addLog(tag, message);
//    }
  }

  @Override
  public void error(String tag, String message, Throwable exception) {
    super.error(tag, message, exception);
//    if (logLevel >= LOG_ERROR) {
      ConsoleSystem.addLog(tag, message, exception);
//    }
  }
}
