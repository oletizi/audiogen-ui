package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.ui.controller.BeatInstrumentEditorController;
import javafx.stage.Stage;
import org.junit.Test;

import java.io.File;

import static com.orionletizi.audiogen.ui.controller.AbstractController.dataStore;
import static com.orionletizi.util.Assertions.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BeatInstrumentEditorTest extends AbstractFXTester {


  private BeatInstrumentEditorController controller;
  private File programFile;

  @Override
  public void start(final Stage stage) throws Exception {
    super.start(stage);

    capturePreHeadlessProps();
    headless();
    programFile = new File(ClassLoader.getSystemResource("sfz/urbanage/urbanage.sfz").getFile());
    controller = loader.getController();
  }

  @Override
  protected String getFxmlPath() {
    return "com/orionletizi/audiogen/ui/beat-instrument-editor-pane.fxml";
  }


  @Test
  public void testValidation() throws Exception {
    assertTrue(controller.getSaveButton().isDisabled());
    assertFalse(controller.getChooseButton().isDisabled());

    when(dchooser.showDialog()).thenReturn(programFile.getParentFile());
    clickOn("#chooseButton");

    // we haven't set the name yet, so the save button should still be disabled
    assertTrue(controller.getSaveButton().isDisabled());

    clickOn("#pathField");
    typeString("my/path");

    // click on something else to move focus out of the path field
    clickOn("#dirField");

    // now the instrument should be valid
    assertFalse(controller.getSaveButton().isDisabled());

    // now delete the path text...
    clickOn("#pathField");
    controller.getPathField().setText("");
    clickOn("#dirField");
    // the instrument should be invalid
    assertTrue(controller.getSaveButton().isDisabled());

    clickOn("#pathField");
    typeString("my/path");
    clickOn("#nameField");

    // the instrument should be valid again
    assertFalse(controller.getSaveButton().isDisabled());

    // now fuck up the name field
    controller.getNameField().setText("");
    clickOn("#dirField");
    assertTrue(controller.getSaveButton().isDisabled());
  }

  @Test
  public void testChooseAndSave() throws Exception {
    //
    // 1. Choose directory
    //    a) there's only an sfz file
    //    b) there's an sfz file and an attributes file.
    //
    when(dchooser.showDialog()).thenReturn(programFile.getParentFile());
    clickOn("#chooseButton");
    verify(dchooser).showDialog();
    final BeatInstrument instrument = controller.getInstrument();
    assertEquals("urbanage", instrument.getName());
    assertEquals("urbanage", controller.getNameField().getText());
    assertEquals("", controller.getPathField().getText());

    final String path = "/path/to/instrument";
    clickOn("#pathField");
    controller.getPathField().setText(path);
    clickOn("#dirField");
    assertFalse("Save button is disabled but shouldn't be", controller.getSaveButton().isDisabled());
    assertEquals(path, instrument.getPath());

    clickOn("#saveButton");
    final BeatInstrument loadedInstrument = dataStore.loadInstrument(instrument.getPath(), instrument.getName(), BeatInstrument.class);

    assertNotNull(loadedInstrument);

    // Now load another instrument (from outside the library)
    clickOn("#chooseButton");
    assertEquals("", controller.getPathField().getText());
  }

}
