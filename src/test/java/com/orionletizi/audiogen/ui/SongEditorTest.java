package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.net.URL;

import static com.orionletizi.util.Assertions.assertTrue;

public class SongEditorTest extends ApplicationTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File localLib;


  @Override
  public void start(final Stage stage) throws Exception {
    localLib = tmp.newFolder();
    AbstractController.dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());


    final String fxmlPath = "com/orionletizi/audiogen/ui/song-editor.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();//FXMLLoader.load(fxmlUrl);

    final MainController controller = loader.<MainController>getController();
    new Thread(() -> controller.getAudioContext().start()).start();


    Scene scene = new Scene(root);
    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testBasics() throws Exception {
    final String songPath = "abc";
    clickOn("#newSongButton");
    clickOn("#songPathField");
    typeString("abc").push(KeyCode.ENTER);
    clickOn("#saveSongButton");

    final File expectedSongFile = new File(localLib, "songs/abc/song.json");
    assertTrue(expectedSongFile.exists());
  }

  private void info(final String s) {
    System.out.println(s);
  }

  private FxRobot typeString(final String string) {
    FxRobot rv = null;
    for (char c : string.toCharArray()) {
      switch (c) {
        case 'a':
          rv = type(KeyCode.A);
          break;
        case 'b':
          rv = type(KeyCode.B);
          break;
        case 'c':
          rv = type(KeyCode.C);
          break;
        default:
          break;
      }
    }
    return rv;
  }

}
