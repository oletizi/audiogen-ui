package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.config.g2.DataStoreConfigG2;
import com.orionletizi.audiogen.samplersong.io.SamplerSongDataStore;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.MainController;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import com.orionletizi.sequencer.Sequencer;
import com.orionletizi.util.logging.LoggerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SongEditor extends Application {
  static {
    LoggerImpl.turnOff(SfzSamplerProgram.class);
    LoggerImpl.turnOff(SfzParser.class);
  }
  public static void main(String[] args) {
    LoggerImpl.turnOff(Sequencer.class);
    if (args.length > 0) {
      AbstractController.songPath = args[0];
    }

    File home = new File(System.getProperty("user.home"));
    File root = new File(home, "audiogen-data-test");
    File localLib = new File(root, "data");
    File localWriteRoot = new File(root, "out");
    URL resourceLib = null;
    try {
      resourceLib = localLib.toURI().toURL();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    // XXX: This is dumb
    AbstractController.dataStore = new SamplerSongDataStore(new DataStoreConfigG2(resourceLib, localLib, localWriteRoot));

    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    final String myPackage = getClass().getPackage().getName().replaceAll("\\.", "/");
    final String fxmlPath = "com/orionletizi/audiogen/ui/song-editor.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    info("fxmlPath: " + fxmlPath + ", fxmlUrl: " + fxmlUrl);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();//FXMLLoader.load(fxmlUrl);

    Scene scene = new Scene(root);

    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
    final MainController controller = (MainController) loader.getController();
    controller.setStage(stage);
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

}
