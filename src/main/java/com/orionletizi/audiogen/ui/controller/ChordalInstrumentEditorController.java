package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.ChordalInstrument;
import com.orionletizi.audiogen.domain.ChordalInstrumentPattern;
import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.ui.view.DChooser;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import com.orionletizi.sampler.Region;
import com.orionletizi.sampler.SamplerProgramParserException;
import com.orionletizi.util.Assertions;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.jfugue.theory.Note;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class ChordalInstrumentEditorController extends AbstractController {

  private ChordalInstrument instrument;
  @FXML
  private Button chooseInstrumentButton;
  @FXML
  private TextField dirField;
  @FXML
  private TextField instrumentPathField;
  @FXML
  TextField instrumentNameField;
  @FXML
  private ListView chordalPatternListView;
  @FXML
  private VBox chordalPatternEditorBox;
  @FXML
  private Button saveInstrumentButton;
  private ObservableList<PatternDisplay> selectedItems;

  @Override
  @SuppressWarnings("unchecked")
  public void initialize(URL location, ResourceBundle resources) {

    final FXMLLoader loader = getChordalInstrumentPatternEditorLoader();

    try {
      Parent chordalPatternEditor = loader.load();
      chordalPatternEditorBox.getChildren().add(chordalPatternEditor);

      final ChordalInstrumentPatternEditorController controller = loader.getController();

      Assertions.assertNotNull(controller);

      chooseInstrumentButton.setOnAction(event -> chooseInstrument());
      saveInstrumentButton.setOnAction(event -> saveInstrument());

      instrumentPathField.textProperty().addListener((observable, oldValue, newValue) -> {
        setPath();
      });

      selectedItems = chordalPatternListView.getSelectionModel().getSelectedItems();
      selectedItems.addListener((ListChangeListener) c -> {
        if (!selectedItems.isEmpty()) {
          final PatternDisplay patternDisplay = selectedItems.get(0);
          controller.setChordalPattern(patternDisplay.pattern, patternDisplay.regions);
        }
      });

      disableEditors(true);
      saveInstrumentButton.setDisable(true);
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "Error Initializing", e);
      throw new RuntimeException(e);
    }
  }

  private void setPath() {
    instrument.setPath(instrumentPathField.getText());
    saveInstrumentButton.setDisable(!isValid());
  }

  @SuppressWarnings("unchecked")
  private void chooseInstrument() {
    final DChooser chooser = getDirectoryChooser();
    assert chooser != null;
    chooser.setTitle("Choose Program Directory");
    final DataStore dataStore = getDataStore();
    assert dataStore != null;
    chooser.setInitialDirectory(dataStore.getLocalInstrumentLib());
    final File dir = chooser.showDialog();
    if (dir != null) {
      try {
        loadInstrument(dir);
      } catch (Throwable e) {
        error(e);
      }
    }
  }

  private void loadInstrument(final File dir) throws IOException, SamplerProgramParserException {
    info("loading instrument: " + dir);
    final File attributes = dataStore.getInstrumentAttributesFile(dir);
    if (attributes != null) {
      instrument = dataStore.loadInstrument(attributes, ChordalInstrument.class);

    } else {
      instrument = new ChordalInstrument();
      final File programFile = dataStore.getSamplerProgramFile(dir);
      if (programFile == null) {
        error("No Instrument Found", "No Instrument Found", "No instrument attributes or program file found.");
        return;
      }
      instrument.setName(dir.getName());
      instrument.setSourceURL(programFile.toURI().toURL());
    }
    chordalPatternListView.getItems().clear();
    for (int i = 0; i < 128; i++) {
      final Set<Region> regions = instrument.getSamplerProgram(dataStore).getRegionsByKey(i);
      final Map<Integer, ChordalInstrumentPattern> patterns = instrument.getPatterns();
      if (regions == null || regions.isEmpty()) {
        // check to see if there's a region in the instrument and delete it
        info("No region for key: " + i);
        patterns.remove(i);
      } else {
        ChordalInstrumentPattern pattern = patterns.get(i);
        if (pattern == null) {
          pattern = new ChordalInstrumentPattern(Tempo.newTempoFromBPM(120), new TimeSignature(4, 4), 1, new ChordStructure(), i);
          patterns.put(i, pattern);
        }
        chordalPatternListView.getItems().add(new PatternDisplay(pattern, regions));
      }
    }
    updateFields(dir);
    info("Done loading instrument: " + dir);
  }

  private void updateFields(final File dir) {
    dirField.setText(dir.getAbsolutePath());
    instrumentPathField.setText(instrument.getPath());
    instrumentNameField.setText(instrument.getName());
    disableEditors(false);
    saveInstrumentButton.setDisable(!isValid());
  }

  public boolean isValid() {
    final Validator validator = getValidator();
    return !validator.isEmpty(instrument.getPath()) && !validator.isEmpty(instrument.getName());
  }

  private void saveInstrument() {
    try {
      instrument = dataStore.saveInstrument(instrument);
      final File dir = new File(instrument.getSourceURL().getFile());
      updateFields(dir);
    } catch (IOException e) {
      error(e);
    }
  }

  private void disableEditors(boolean b) {
    instrumentPathField.setDisable(b);

  }

  public TextField getInstrumentPathField() {
    return instrumentPathField;
  }

  public Button getSaveInstrumentButton() {
    return saveInstrumentButton;
  }

  public TextField getDirField() {
    return dirField;
  }

  public ChordalInstrument getInstrument() {
    return instrument;
  }

  public ListView getChordalPatternListView() {
    return chordalPatternListView;
  }

  private static class PatternDisplay {
    private final ChordalInstrumentPattern pattern;
    private Set<Region> regions;

    private PatternDisplay(final ChordalInstrumentPattern pattern, final Set<Region> regions) {
      this.pattern = pattern;
      this.regions = regions;
    }

    public String toString() {
      return "Key: " + pattern.getSamplerNote() + " (" + new Note(pattern.getSamplerNote()) + ")";
    }
  }
}
