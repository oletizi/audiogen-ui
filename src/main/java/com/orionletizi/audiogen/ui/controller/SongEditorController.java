package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.domain.ChordalPattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class SongEditorController implements Initializable {

  protected static final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
  protected static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    ac.start();
  }

  protected Map<Integer, ChordalPattern> instrumentPatterns = new TreeMap<>();
  protected File instrumentProgramFile;

  @FXML
  private MenuBar menuBar;

  @FXML
  private InstrumentPaneController instrumentPaneController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    final Menu menuFile = new Menu("File");
    final MenuItem saveItem = new MenuItem("Save");
    saveItem.setOnAction(event -> {
      save();
    });
    saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));

    final MenuItem quitItem = new MenuItem("Quit");
    quitItem.setOnAction(event -> {
      quit();
    });
    quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));

    menuFile.getItems().add(saveItem);
    menuFile.getItems().add(quitItem);
    menuBar.getMenus().clear();
    menuBar.getMenus().add(menuFile);
  }

  protected void quit() {
    // TODO: implement a save or cancel dialog
    save();
    ac.stop();
    System.exit(0);
  }

  protected void save() {
    try {
      info("Saving...");
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File(instrumentProgramFile.getParentFile(), "attributes.json"), instrumentPatterns);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

  public void setStage(Stage stage) {

  }
}
