package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.samplersong.domain.BeatInstrument;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BeatInstrumentPaneController extends AbstractController {

  @FXML
  private Button openInstrumentButton;
  @FXML
  private Button newInstrumentButton;
  @FXML
  private TextField instrumentPathDisplay;
  @FXML
  private BeatInstrument beatInstrument;

  private boolean isNew = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setDisableEditor(true);
    openInstrumentButton.setOnAction(event -> openInstrument());
    newInstrumentButton.setOnAction(event -> newInstrument());
  }

  private void newInstrument() {
    isNew = true;
    loadInstrument(new BeatInstrument());
  }

  private void openInstrument() {
    isNew = false;
    final File file = new FileChooser().showOpenDialog(null);
    if (file != null) {
      try {
        loadInstrument(getMapper().readValue(file, BeatInstrument.class));
      } catch (IOException e) {
        error("Error", "Error Opening Instrument", e.getMessage());
      }
    }
  }

  private void loadInstrument(BeatInstrument beatInstrument) {
    this.beatInstrument = beatInstrument;
    setDisableEditor(false);
  }

  private void setDisableEditor(boolean disable) {

  }
}
