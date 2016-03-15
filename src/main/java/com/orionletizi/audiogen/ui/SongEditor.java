package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.MainController;
import com.orionletizi.sequencer.Sequencer;
import com.orionletizi.util.logging.LoggerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SongEditor extends Application {
  static {
    //LoggerImpl.turnOff(SfzSamplerProgram.class);
    //LoggerImpl.turnOff(SfzParser.class);
    LoggerImpl.turnOff(Sequencer.class);
  }
  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      AbstractController.songPath = args[0];
    }

    File home = new File(System.getProperty("user.home"));
    File root = new File(home, "audiogen-data");
    File localLib = new File(root, "1.0/lib");
    AbstractController.dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());

    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    final String fxmlPath = "com/orionletizi/audiogen/ui/song-editor.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    info("fxmlPath: " + fxmlPath + ", fxmlUrl: " + fxmlUrl);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();//FXMLLoader.load(fxmlUrl);

    final MainController controller = loader.<MainController>getController();
    new Thread(() -> controller.getAudioContext().start()).start();


    Scene scene = new Scene(root);
    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

}
