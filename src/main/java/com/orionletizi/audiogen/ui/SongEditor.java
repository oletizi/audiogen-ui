package com.orionletizi.audiogen.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SongEditor extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    final String myPackage = getClass().getPackage().getName().replaceAll("\\.", "/");
    final String fxmlPath = "com/orion/orionletizi/audiogen/ui/songeditor.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    info("fxmlPath: " + fxmlPath + ", fxmlUrl: " + fxmlUrl);
    info("classpath: " + System.getProperty("java.class.path"));
    Parent root = FXMLLoader.load(fxmlUrl);

    Scene scene = new Scene(root);

    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

}
