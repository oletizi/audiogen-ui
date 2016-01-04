package com.orionletizi.audiogen.ui.player;

public interface Player {
  void play();

  void pause(boolean shouldPause);

  void stop();

  String getSource();

  boolean isPaused();
}
