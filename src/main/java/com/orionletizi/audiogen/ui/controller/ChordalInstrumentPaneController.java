package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.samplersong.domain.ChordalInstrument;
import com.orionletizi.audiogen.samplersong.domain.ChordalInstrumentPattern;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import com.orionletizi.sampler.sfz.Region;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
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
import javafx.stage.FileChooser;
import org.jfugue.theory.Note;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class ChordalInstrumentPaneController extends AbstractController {

  private ChordalInstrument instrument;
  @FXML
  private Button chooseInstrumentButton;
  @FXML
  private TextField instrumentPathDisplay;
  @FXML
  private ListView chordalPatternListView;
  @FXML
  private VBox chordalPatternEditorBox;
  @FXML
  private Button saveInstrumentButton;
  private ObservableList<PatternDisplay> selectedItems;
  private File instrumentAttributesFile;


  @Override
  @SuppressWarnings("unchecked")
  public void initialize(URL location, ResourceBundle resources) {

    final FXMLLoader loader = getChordalInstrumentPatternEditorLoader();

    try {
      Parent chordalPatternEditor = loader.<Parent>load();
      chordalPatternEditorBox.getChildren().add(chordalPatternEditor);

      final ChordalInstrumentPatternEditorController controller = loader.<ChordalInstrumentPatternEditorController>getController();

      Assertions.assertNotNull(controller);

      chooseInstrumentButton.setOnAction(event -> chooseInstrument());
      saveInstrumentButton.setOnAction(event -> saveInstrument());

      selectedItems = chordalPatternListView.getSelectionModel().getSelectedItems();
      selectedItems.addListener((ListChangeListener) c -> {
        if (!selectedItems.isEmpty()) {
          final PatternDisplay patternDisplay = selectedItems.get(0);
          controller.setChordalPattern(patternDisplay.pattern, patternDisplay.regions);
        }
      });

      disableEditors(true);
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "Error Initializing", e);
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private void chooseInstrument() {
    final FileChooser chooser = new FileChooser();
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      try {
        final SfzSamplerProgram program = new SfzSamplerProgram(new SfzParser(), file);
        instrumentAttributesFile = new File(file.getParentFile(), "instrument-attributes.json");
        if (instrumentAttributesFile.isFile()) {
          instrument = getMapper().readValue(instrumentAttributesFile, ChordalInstrument.class);
        } else {
          instrument = new ChordalInstrument();
          instrument.setName(file.getParentFile().getName());
          instrument.setSamplerProgramFile(file);
        }
        instrumentPathDisplay.setText(file.getAbsolutePath());
        chordalPatternListView.getItems().clear();
        for (int i = 0; i < 128; i++) {
          final Set<Region> regions = program.getRegionsByKey(i);
          final Map<Integer, ChordalInstrumentPattern> patterns = instrument.getPatterns();
          if (regions == null) {
            // check to see if there's a region in the instrument and delete it
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
        disableEditors(false);
      } catch (Exception e) {
        e.printStackTrace();
        error("Error", "Error Loading Sampler Program", e);
      }
    }
  }

  private void saveInstrument() {
    try {
      getMapper().writeValue(instrumentAttributesFile, instrument);
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "Error Saving Instrument", e);
    }
  }

  private void disableEditors(boolean b) {
    saveInstrumentButton.setDisable(b);
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
