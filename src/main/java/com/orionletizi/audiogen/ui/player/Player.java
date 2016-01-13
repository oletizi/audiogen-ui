package com.orionletizi.audiogen.ui.player;

public interface Player {
  void play();

  void pause(final boolean shouldPause);

  void stop();

  String getSource();

  boolean isPaused();

  void setPlayerObserver(final PlayerObserver observer);
}
