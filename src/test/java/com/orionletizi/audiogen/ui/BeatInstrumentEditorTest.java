package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.BeatInstrumentEditorController;
import com.orionletizi.audiogen.ui.view.AlertFactory;
import com.orionletizi.audiogen.ui.view.DChooser;
import com.orionletizi.audiogen.ui.view.FChooser;
import com.orionletizi.audiogen.ui.view.IAlert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;

import static com.orionletizi.util.Assertions.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class BeatInstrumentEditorTest extends AbstractFXTester {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File localLib;
  private DataStore dataStore;
  private FChooser fchooser;
  private DChooser dchooser;
  private IAlert mockAlert;
  private BeatInstrumentEditorController controller;
  private File programFile;

  @Override
  public void start(final Stage stage) throws Exception {
    programFile = new File(ClassLoader.getSystemResource("sfz/urbanage/urbanage.sfz").getFile());

    localLib = tmp.newFolder();

    dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());


    final String fxmlPath = "com/orionletizi/audiogen/ui/beat-instrument-editor-pane.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();
    controller = loader.getController();

    // Set up the mock infrastructure
    fchooser = mock(FChooser.class);
    dchooser = mock(DChooser.class);
    mockAlert = mock(IAlert.class);
    final AlertFactory alertFactory = type -> mockAlert;
    AbstractController.dataStore = dataStore;
    AbstractController.fchooser = fchooser;
    AbstractController.dchooser = dchooser;
    AbstractController.alertFactory = alertFactory;

    Scene scene = new Scene(root);
    stage.setTitle("Beat Instrument Editor (Test)");
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testLifecycleUI() throws Exception {
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

    assertFalse(controller.getSaveButton().isDisabled());
  }

  @Test
  public void testChoose() throws Exception {
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

    clickOn("#saveButton");
    verify(mockAlert, times(1)).showAndWait();
  }

}
