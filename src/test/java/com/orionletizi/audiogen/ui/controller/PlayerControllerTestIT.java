package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.ui.player.AudioPlayer;
import com.orionletizi.audiogen.ui.player.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

import java.io.File;
import java.net.URL;

public class PlayerControllerTestIT extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
    ac.start();
    final File audioFile = new File(ClassLoader.getSystemResource("sfz/guitar3/samples/sample-lokey4-hikey4-lovel0-hivel96.wav").getFile());
    assert audioFile != null;

    final String path = "com/orionletizi/audiogen/ui/player.fxml";
    final URL url = ClassLoader.getSystemResource(path);
    assert url != null;
    final FXMLLoader loader = new FXMLLoader(url);
    final Parent root = loader.load();
    final Scene scene = new Scene(root);

    final PlayerController controller = loader.<PlayerController>getController();
    Player player = new AudioPlayer(ac, audioFile);
    controller.setPlayer(player);

    stage.setTitle(getClass().getSimpleName());
    stage.setScene(scene);
    stage.show();
  }

  public static void main(final String[] args) {
    launch(args);
  }
}