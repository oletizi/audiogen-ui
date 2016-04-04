package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.ui.controller.ChordalInstrumentEditorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static com.orionletizi.util.Assertions.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class ChordalInstrumentEditorTest extends AbstractFXTester {
  private ChordalInstrumentEditorController controller;

  @Override
  public void start(final Stage stage) throws Exception {
    final String fxmlPath = "com/orionletizi/audiogen/ui/chordal-instrument-editor-pane.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();
    controller = loader.getController();

    Scene scene = new Scene(root);
    stage.setTitle("ChordalInstrumentPane (Test)");
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testBasics() throws Exception {
    final String guitarFolderPath = "sfz/guitar3";
    final File guitarFolder = new File(ClassLoader.getSystemResource(guitarFolderPath).getFile());
    final TextField dirField = controller.getDirField();
    final TextField instrumentPathField = controller.getInstrumentPathField();
    final Button saveInstrumentButton = controller.getSaveInstrumentButton();

    // before anything happens, all the fields and buttons should be disabled.
    assertFalse(dirField.isEditable());
    assertTrue(instrumentPathField.isDisabled());
    assertTrue(saveInstrumentButton.isDisabled());

    assertEquals("", instrumentPathField.getText());
    when(dchooser.showDialog()).thenReturn(guitarFolder);

    // TEST: Choose an instrument
    clickOn("#chooseInstrumentButton");

    // after the instrument is chosen, check to see that the editable fields are not disabled
    // and that the save button is disabled (until it's valid
    assertFalse(dirField.isEditable());
    assertEquals(guitarFolder.getAbsolutePath(), dirField.getText());

    assertFalse(instrumentPathField.isDisabled());
    assertTrue(saveInstrumentButton.isDisabled());
    System.out.println("instrument path display: " + instrumentPathField.getText());

    // TEST: set the path
    clickOn("#instrumentPathField");
    typeString("party/music/");

    assertFalse(saveInstrumentButton.isDisabled());
  }
}
