package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.ui.controller.BeatInstrumentPatternEditorController;
import javafx.stage.Stage;
import org.junit.Test;

public class BeatInstrumentPatternEditorTest extends AbstractFXTester {

  private BeatInstrumentPatternEditorController controller;

  @Override
  public void start(final Stage stage) throws Exception {
    super.start(stage);
    controller = loader.getController();
  }

  @Test
  public void testBasics() throws Exception {

  }

  @Override
  protected String getFxmlPath() {
    return "com/orionletizi/audiogen/ui/beat-instrument-pattern-editor.fxml";
  }
}
