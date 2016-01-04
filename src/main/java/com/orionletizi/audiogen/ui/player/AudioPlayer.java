package com.orionletizi.audiogen.ui.player;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.SamplePlayer;

import java.io.File;
import java.io.IOException;

public class AudioPlayer implements Player {

  private final AudioContext ac;
  private final File audioFile;
  private final SamplePlayer player;

  public AudioPlayer(final AudioContext ac, final File audioFile) throws IOException {
    this.ac = ac;
    this.audioFile = audioFile;
    this.player = new SamplePlayer(ac, new Sample(audioFile.getAbsolutePath()));
    player.setKillOnEnd(false);
    ac.out.addInput(player);
    ac.out.addDependent(player);
  }

  @Override
  public void play() {
    if (!ac.isRunning()) {
      ac.start();
    }
    player.start();
    player.pause(false);
  }

  @Override
  public void pause(boolean shouldPause) {
    player.pause(shouldPause);
  }

  @Override
  public void stop() {
    pause(true);
    player.setPosition(0);
  }

  @Override
  public String getSource() {
    return audioFile.getAbsolutePath();
  }

  @Override
  public boolean isPaused() {
    return player.isPaused();
  }
}
