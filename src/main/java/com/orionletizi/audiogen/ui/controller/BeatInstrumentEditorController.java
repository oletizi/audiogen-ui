package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.ui.view.DChooser;
import com.orionletizi.sampler.SamplerProgramParserException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BeatInstrumentEditorController extends AbstractController {

  private BeatInstrument instrument = new BeatInstrument();

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
    instrument.setName("");
    saveButton.setDisable(true);
    chooseButton.setOnAction(event -> chooseInstrument());
    saveButton.setOnAction(event -> saveInstrument());
    pathField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      info("pathField focus changed! is focused: " + newValue);
      if (!newValue) { // focus was lost
        setPath();
      }
    });
    nameField.focusedProperty().addListener((observable, oldValue, newValue)->{
      info("nameField focus changed! is focused: " + newValue);
      if (!newValue) {
        setName();
      }
    });
  }

  private void setName() {
    final String name = nameField.getText();
    info("setName(): " + name);
    instrument.setName(name);
    validateInstrument();
    if (getValidator().isEmpty(instrument.getName())) {
      error("Please Set Instrument Name", "Please Set Instrument Name", "Please set the instrument name.");
    }
  }

  private void setPath() {
    final String path = pathField.getText();
    info("setPath(): " + path);
    instrument.setPath(path);
    validateInstrument();
    if (getValidator().isEmpty(instrument.getPath())) {
      error("Please Set Instrument Path", "Please Set Instrument Path", "Please set the instrument path.");
    }
  }

  private void saveInstrument() {
    if (instrument == null) {
      error("Please Choose Instrument", "Please Choose Instrument", "Please Choose Instrument");
    } else if (! validateInstrument()){
      error("WTF?!", "WTF?", "The instrument isn't valid for some reason.");
    } else {
      try {
        instrument = dataStore.saveInstrument(instrument);
        updateInstrument();
      } catch (IOException e) {
        error(e);
      }
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
    updateInstrument();
  }

  private void updateInstrument() throws IOException {
    try {
      dirField.setText("" + instrument.getSamplerProgram(dataStore).getProgramResource());
    } catch (final SamplerProgramParserException e) {
      error(e);
    }
    nameField.setText(instrument.getName());
    pathField.setText(instrument.getPath());
    validateInstrument();
  }

  private boolean validateInstrument() {
    info("Validating instrument...");
    boolean rv = instrument != null
        && !getValidator().isEmpty(instrument.getName())
        && !getValidator().isEmpty(instrument.getPath());
    info("Instrument valid: " + rv);
    pathField.setDisable(instrument == null);
    nameField.setDisable(instrument == null);
    saveButton.setDisable(!rv);
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
