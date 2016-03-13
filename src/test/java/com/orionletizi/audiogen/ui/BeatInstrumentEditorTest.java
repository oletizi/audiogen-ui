package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.BeatInstrumentEditorController;
import com.orionletizi.audiogen.ui.view.AlertFactory;
import com.orionletizi.audiogen.ui.view.FChooser;
import com.orionletizi.audiogen.ui.view.IAlert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BeatInstrumentEditorTest extends ApplicationTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File localLib;
  private DataStore dataStore;
  private FChooser chooser;
  private IAlert mockAlert;
  private BeatInstrumentEditorController controller;

  @Override
  public void start(final Stage stage) throws Exception {

    localLib = tmp.newFolder();
    dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());


    final String fxmlPath = "com/orionletizi/audiogen/ui/beat-instrument-editor-pane.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();
    controller = loader.getController();

    // Set up the mock infrastructure
    chooser = mock(FChooser.class);
    mockAlert = mock(IAlert.class);
    final AlertFactory alertFactory = type -> mockAlert;
    AbstractController.dataStore = dataStore;
    AbstractController.fileChooser = chooser;
    AbstractController.alertFactory = alertFactory;

    Scene scene = new Scene(root);
    stage.setTitle("Beat Instrument Editor (Test)");
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testChoose() throws Exception {
    final File programFile = new File(ClassLoader.getSystemResource("sfz/urbanage/urbanage.sfz").getFile());
    when(chooser.showOpenDialog()).thenReturn(programFile);
    clickOn("#chooseButton");
    verify(chooser).showOpenDialog();
    assertEquals(programFile, controller.getProgramFile());
    assertEquals("urbanage", controller.getProgramField().getText());
  }

}
