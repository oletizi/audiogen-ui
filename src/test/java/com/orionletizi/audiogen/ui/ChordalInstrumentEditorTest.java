package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.domain.ChordalInstrument;
import com.orionletizi.audiogen.domain.ChordalInstrumentPattern;
import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.ui.controller.ChordalInstrumentEditorController;
import com.orionletizi.music.theory.Tempo;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Test;

import java.io.File;

import static com.orionletizi.util.Assertions.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class ChordalInstrumentEditorTest extends AbstractFXTester {
  private ChordalInstrumentEditorController controller;

  @Override
  public void start(final Stage stage) throws Exception {
    super.start(stage);
    controller = loader.getController();
  }

  @Override
  protected String getFxmlPath() {
    return "com/orionletizi/audiogen/ui/chordal-instrument-editor-pane.fxml";
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

    final ListView<ChordalInstrumentEditorController.PatternDisplay> listView = controller.getChordalPatternListView();
    assertFalse(listView.getItems().isEmpty());


    // TEST: set the path
    clickOn("#instrumentPathField");
    typeString("party");

    assertFalse(saveInstrumentButton.isDisabled());


    // TEST: set the pattern for one of the keys
    listView.getSelectionModel().selectFirst();
    ChordalInstrumentPattern pattern = listView.getItems().get(0).getPattern();
    doubleClickOn("#tempoField");

    typeString("121");

    assertEquals(Tempo.newTempoFromBPM(121), pattern.getTempo());

    // TEST: save the instrument
    clickOn("#saveInstrumentButton");

    // Make sure that the pattern list is not empty
    assertFalse(listView.getItems().isEmpty());

    // Make sure the pattern edit survived.
    pattern = listView.getItems().get(0).getPattern();
    assertEquals(Tempo.newTempoFromBPM(121), pattern.getTempo());

    // Make sure the pattern in the instrument is the same as the pattern in the list view
    ChordalInstrument instrument = controller.getInstrument();
    assertEquals(pattern, instrument.getPatterns().get(pattern.getSamplerNote()));

    // Make sure the instrument got copied to the right place
    final DataStore dataStore = getDataStore();
    final File instrumentLib = dataStore.getLocalInstrumentLib();
    File newDir = new File(instrumentLib, instrument.getPath() + "/guitar3");
    assertEquals(newDir.getAbsolutePath(), dirField.getText());
  }
}
