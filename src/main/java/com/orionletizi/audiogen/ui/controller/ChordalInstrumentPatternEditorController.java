package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.samplersong.domain.ChordalInstrumentPattern;
import com.orionletizi.sampler.sfz.Region;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ChordalInstrumentPatternEditorController extends AbstractController {
  @FXML
  private ChordalPatternEditorController chordalPatternEditorController;
  @FXML
  private VBox auditionBox;

  public void setChordalPattern(final ChordalInstrumentPattern pattern, final Set<Region> regions) {
    chordalPatternEditorController.setChordalPattern(pattern);
    // Clear the current audio players
    info("Preparing the auditionBox: " + auditionBox);
    auditionBox.getChildren().clear();
    // Set up the new audio players
    for (Region region : regions) {
      try {
        final PlayerController.Loader loader = new PlayerController.Loader(getAudioContext(), new File(region.getSample().getFileName()));
        auditionBox.getChildren().add(loader.getUI());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
