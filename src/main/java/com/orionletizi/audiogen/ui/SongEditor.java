package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.ui.controller.SongEditorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SongEditor extends Application {

  public static File programFile;

  public static void main(String[] args) {
    if (args.length > 0) {
      final File file = new File(args[0]);
      if (file.isFile() && file.getName().endsWith(".sfz")) {
        programFile = file;
      }
    }
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    final String myPackage = getClass().getPackage().getName().replaceAll("\\.", "/");
    final String fxmlPath = "com/orion/orionletizi/audiogen/ui/songeditor.fxml";
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    info("fxmlPath: " + fxmlPath + ", fxmlUrl: " + fxmlUrl);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();//FXMLLoader.load(fxmlUrl);

    Scene scene = new Scene(root);

    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
    final SongEditorController controller = (SongEditorController) loader.getController();
    controller.setStage(stage);
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

}
