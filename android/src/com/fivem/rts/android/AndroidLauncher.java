package com.fivem.rts.android;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fivem.rts.GoogleServicesInterface;
import com.fivem.rts.SpaceRts;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements GoogleServicesInterface {
  GameHelper gameHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new SpaceRts(this), config);

    gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
    gameHelper.enableDebugLog(true);

    GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
      @Override
      public void onSignInSucceeded() {
      }

      @Override
      public void onSignInFailed() {
      }
    };

    gameHelper.setup(gameHelperListener);
  }


  @Override
  protected void onStart() {
    super.onStart();
    gameHelper.onStart(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    gameHelper.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    gameHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void signin() {
  }

  @Override
  public void signout() {

  }
}

