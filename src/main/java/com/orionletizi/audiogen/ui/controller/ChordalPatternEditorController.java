package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.ChordStructurePattern;
import com.orionletizi.audiogen.ui.view.ChordStructureEditor;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChordalPatternEditorController extends AbstractController {

  @FXML
  private TextField tempoField;
  @FXML
  private TextField timeSignatureBeatsPerMeasureField;
  @FXML
  private TextField timeSignatureBeatDivisionField;
  @FXML
  private VBox chordStructureBox;


  private ChordStructurePattern chordalPattern;

  public void setChordalPattern(final ChordStructurePattern chordalPattern) {
    this.chordalPattern = chordalPattern;
    tempoField.setText(chordalPattern.getTempo().getBPM() + "");

    final TimeSignature timeSignature = chordalPattern.getTimeSignature();
    timeSignatureBeatsPerMeasureField.setText(timeSignature.getBeatsPerBar() + "");
    timeSignatureBeatDivisionField.setText(timeSignature.getBeatUnit() + "");

    final ChordStructure chordStructure = chordalPattern.getStructure();
    chordStructureBox.getChildren().clear();
    chordStructureBox.getChildren().add(new ChordStructureEditor(chordStructure));
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
