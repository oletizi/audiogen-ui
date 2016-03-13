package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.ui.view.DChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BeatInstrumentEditorController extends AbstractController {

  private BeatInstrument instrument;

  @FXML
  private Button chooseButton;
  @FXML
  private Button saveButton;
  @FXML
  private TextField dirField;
  @FXML
  private TextField nameField;
  @FXML
  private TextField pathField;

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    saveButton.setDisable(true);
    chooseButton.setOnAction(event -> chooseInstrument());
    saveButton.setOnAction(event -> saveInstrument());
    pathField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      info("Focus changed! is focused: " + newValue);
      if (!newValue) { // focus was lost
        setPath();
      }
    });
  }

  private void setPath() {

    final String path = pathField.getText();
    info("setPath(): " + path);
    if (getValidator().isEmpty(path)) {
      info("Oops! The path is empty.");
      error("Please Set Instrument Path", "Please Set Instrument Path", "Please set the instrument path.");
    } else {
      info("Setting instrument path to: " + path);
      instrument.setPath(path);
      validateInstrument();
    }
  }

  private void saveInstrument() {
    if (instrument == null) {
      error("Please Choose Instrument", "Please Choose Instrument", "Please Choose Instrument");
    }
  }

  private void chooseInstrument() {
    final DChooser chooser = getDirectoryChooser();
    chooser.setTitle("Choose Program File");
    chooser.setInitialDirectory(getDataStore().getLocalInstrumentLib());
    final File dir = chooser.showDialog();
    if (dir != null) {
      try {
        loadInstrument(dir);
      } catch (IOException e) {
        error(e);
      }
    }
  }

  private void loadInstrument(final File dir) throws IOException {
    final File attributes = dataStore.getInstrumentAttributesFile(dir);
    if (attributes != null) {
      instrument = dataStore.loadInstrument(attributes, BeatInstrument.class);
    } else {
      instrument = new BeatInstrument();
      final File programFile = dataStore.getSamplerProgramFile(dir);
      if (programFile == null) {
        error("No Instrument Found", "No Instrument Found", "No instrument attributes or program file found.");
        return;
      }
      instrument.setName(dir.getName());
      instrument.setSourceURL(programFile.toURI().toURL());
    }

    nameField.setText(instrument.getName());
    validateInstrument();
  }

  private boolean validateInstrument() {
    info("Validating instrument...");
    boolean rv = instrument != null
        && !getValidator().isEmpty(instrument.getName())
        && !getValidator().isEmpty(instrument.getPath());
    info("Instrument valid: " + rv);
    if (rv) {
      saveButton.setDisable(false);
    }
    return rv;
  }

  public BeatInstrument getInstrument() {
    return instrument;
  }

  public TextField getNameField() {
    return nameField;
  }

  public TextField getPathField() {
    return pathField;
  }

  public Button getSaveButton() {
    return saveButton;
  }

  public Button getChooseButton() {
    return chooseButton;
  }
}
