package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.ui.player.AudioPlayer;
import com.orionletizi.audiogen.ui.player.Player;
import com.orionletizi.audiogen.ui.player.SequencerPlayer;
import com.orionletizi.sampler.SamplerProgram;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.beadsproject.beads.core.AudioContext;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController extends AbstractController {

  @FXML
  private Label audioFileLabel;

  @FXML
  private Button playButton;

  @FXML
  private Button stopButton;
  private Player player;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    playButton.setOnAction(event -> togglePlay());
    stopButton.setOnAction(event -> stop());
  }

  private void stop() {
    player.stop();
  }

  private void togglePlay() {
    info("Toggle play: " + player);
    if (player.isPaused()) {
      info("player is paused!");
      player.pause(false);
      playButton.setText("||");
    } else {
      info("player is not paused.");
      player.pause(true);
      playButton.setText(">");
    }
  }

  public void setPlayer(final Player player) {
    this.player = player;
    audioFileLabel.setText(player.getSource());
  }

  public static class Loader {

    private static final String audioPlayerPath = "com/orionletizi/audiogen/ui/player.fxml";
    private final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(audioPlayerPath));
    private final Parent parent;
    private final PlayerController controller;

    private Loader(final Player player) throws IOException {
      parent = loader.<Parent>load();
      controller = loader.<PlayerController>getController();
      controller.setPlayer(player);
    }

    public Loader(final AudioContext ac, final File audioFile) throws IOException {
      this(new AudioPlayer(ac, audioFile));
    }

    public Loader(final AudioContext ac, final SamplerProgram program, final URL midiSource) throws IOException,
        InvalidMidiDataException {
      this(new SequencerPlayer(ac, program, midiSource));
    }

    public PlayerController getController() {
      return controller;
    }

    public Parent getUI() {
      return parent;
    }

  }
}
