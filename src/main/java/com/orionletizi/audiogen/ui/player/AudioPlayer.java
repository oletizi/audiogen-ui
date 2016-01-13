package com.orionletizi.audiogen.ui.player;

import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.SamplePlayer;

import java.io.File;
import java.io.IOException;

public class AudioPlayer implements Player {

  private static final Logger logger = LoggerImpl.forClass(AudioPlayer.class);

  private final AudioContext ac;
  private final File audioFile;
  private final SamplePlayer player;
  private PlayerObserver observer = new PlayerObserverAdapter();

  public AudioPlayer(final AudioContext ac, final File audioFile) throws IOException {
    this.ac = ac;
    this.audioFile = audioFile;
    final Sample sample = new Sample(audioFile.getAbsolutePath());
    info("Sample: " + sample);
    this.player = new SamplePlayer(ac, sample);
    player.setKillOnEnd(false);
    player.pause(true);
    player.setEndListener(new Bead() {
      @Override
      protected void messageReceived(Bead message) {
        info("Message received: " + message);
        if (message != null && message.equals(player)) {
          observer.notifyEnd(AudioPlayer.this);
          player.pause(true);
          player.setPosition(0);
          observer.notifyEnd(AudioPlayer.this);
        }
      }
    });


    ac.out.addInput(player);
    ac.out.addDependent(player);
  }

  @Override
  public void play() {
    info("Starting audio context...");
    ac.start();
    player.start();
    player.pause(false);
  }

  private void info(String s) {
    logger.info(s);
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

  @Override
  public void setPlayerObserver(PlayerObserver observer) {
    this.observer = observer;
  }
}
