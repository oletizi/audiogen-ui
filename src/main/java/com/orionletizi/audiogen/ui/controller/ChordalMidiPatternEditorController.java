package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.ChordalMidiPattern;
import com.orionletizi.sampler.Sampler;
import com.orionletizi.sampler.SamplerProgram;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChordalMidiPatternEditorController extends AbstractController {

  @FXML
  private TextField midiFileField;
  @FXML
  private VBox auditionBox;

  @FXML
  private ChordalPatternEditorController chordalPatternEditorController;

  public void setChordalMidiPattern(final SamplerProgram program, final ChordalMidiPattern pattern) throws InvalidMidiDataException,
      IOException {
    midiFileField.setText("" + pattern.getPath());
    chordalPatternEditorController.setChordalPattern(pattern);
    final List<Receiver> instruments = new ArrayList<>();
    instruments.add(new Sampler(getAudioContext(), program));

    // Clear the current audio player
    info("Preparing the auditionBox: " + auditionBox);
    auditionBox.getChildren().clear();
    // Set up the new audio players
    try {
      final PlayerController.Loader loader = new PlayerController.Loader(getAudioContext(), program, pattern.getSourceURL());
      auditionBox.getChildren().add(loader.getUI());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
