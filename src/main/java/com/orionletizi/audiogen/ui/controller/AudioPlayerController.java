package com.orionletizi.audiogen.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.SamplePlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AudioPlayerController extends AbstractController {

  @FXML
  private Label audioFileLabel;

  @FXML
  private Button playButton;

  @FXML
  private Button stopButton;
  private SamplePlayer player;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    playButton.setOnAction(event -> togglePlay());
    stopButton.setOnAction(event -> stop());
  }

  private void stop() {
    info("stopping...");
    player.pause(true);
    player.setPosition(0);
    info("stopped.");
  }

  private void togglePlay() {
    if (player.isPaused()) {
      player.pause(false);
      playButton.setText("||");
    } else {
      player.pause(true);
      playButton.setText(">");
    }
  }

  public void setAudioFile(File audioFile) {
    info("setAudioFile: audioFile: " + audioFile);
    audioFileLabel.setText(audioFile.getName());
    try {
      final AudioContext ac = getAudioContext();
      player = new SamplePlayer(ac, new Sample(audioFile.getAbsolutePath()));
      player.setKillOnEnd(false);
      info("wiring up player...");
      info("ac is running: " + ac.isRunning());
      if (!ac.isRunning()) {
        new Thread(() -> {
          info("Calling ac.start()...");
          ac.start();
          info("Done calling ac.start().");
        }).start();
      }

      ac.out.addDependent(player);
      ac.out.addInput(player);
      info("Audio io: " + ac.getAudioIO());
      while (!ac.isRunning()) {
        info("ac not running yet.");
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      info("calling player.start()");
      player.start();
      player.pause(true);
      player.setPosition(0);
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "Error Loading Audio File", e);
    }
  }
}
