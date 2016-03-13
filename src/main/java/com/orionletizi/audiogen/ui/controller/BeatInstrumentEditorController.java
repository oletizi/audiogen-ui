package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.ui.view.FChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BeatInstrumentEditorController extends AbstractController {

  @FXML
  private Button chooseButton;
  @FXML
  private TextField programField;

  private File programFile;
  private BeatInstrument instrument;

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    chooseButton.setOnAction(event -> chooseProgramFile());
  }

  private void chooseProgramFile() {
    final FChooser chooser = getFileChooser();
    chooser.setTitle("Choose Program File");
    chooser.setInitialDirectory(getDataStore().getLocalInstrumentLib());
    final File file = chooser.showOpenDialog();
    if (file != null) {
      loadInstrument(file);
    }
  }

  private void loadInstrument(final File file) {
    programFile = file;
    try {
      instrument = dataStore.loadInstrument(file, BeatInstrument.class);
    } catch (IOException e) {
      error(e);
    }
    // TODO: reset everything
  }

  public TextField getProgramField() {
    return programField;
  }

  public File getProgramFile() {
    return programFile;
  }
}
