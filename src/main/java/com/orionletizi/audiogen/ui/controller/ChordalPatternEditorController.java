package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.samplersong.domain.ChordalPattern;
import com.orionletizi.audiogen.ui.view.ChordStructureEditor;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import com.orionletizi.sampler.sfz.Region;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ChordalPatternEditorController extends AbstractController {

  @FXML
  private TextField tempoField;
  @FXML
  private TextField timeSignatureBeatsPerMeasureField;
  @FXML
  private TextField timeSignatureBeatDivisionField;
  @FXML
  private VBox chordStructureBox;
  @FXML
  private VBox auditionBox;

  private ChordalPattern chordalPattern;

  public void setChordalPattern(final ChordalPattern chordalPattern, final Set<Region> regions) {
    this.chordalPattern = chordalPattern;
    tempoField.setText(chordalPattern.getTempo().getBPM() + "");

    final TimeSignature timeSignature = chordalPattern.getTimeSignature();
    timeSignatureBeatsPerMeasureField.setText(timeSignature.getBeatsPerBar() + "");
    timeSignatureBeatDivisionField.setText(timeSignature.getBeatUnit() + "");

    final ChordStructure chordStructure = chordalPattern.getChordStr();
    chordStructureBox.getChildren().clear();
    chordStructureBox.getChildren().add(new ChordStructureEditor(chordStructure));

    // Clear the current audio players
    info("Preparing the auditionBox: " + auditionBox);
    auditionBox.getChildren().clear();
    // Set up the new audio players
    for (Region region : regions) {
      try {
        final AudioPlayerController.Loader loader = new AudioPlayerController.Loader(new File(region.getSample().getFileName()));
        auditionBox.getChildren().add(loader.getUI());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tempoField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!"".equals(newValue)) {
        chordalPattern.setTempo(Tempo.newTempoFromBPM(Double.parseDouble(newValue)));
      }
    });

    timeSignatureBeatsPerMeasureField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!"".equals(newValue)) {
        chordalPattern.getTimeSignature().setBeatsPerBar(Integer.parseInt(newValue));
      }
    });

    timeSignatureBeatDivisionField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!"".equals(newValue)) {
        chordalPattern.getTimeSignature().setBeatUnit(Integer.parseInt(newValue));
      }
    });
  }
}
