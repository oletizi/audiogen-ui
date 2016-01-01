package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.io.SamplerSongDataStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

public abstract class AbstractController implements Initializable {
  private static final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
  private static final ObjectMapper mapper = new ObjectMapper();
  public static SamplerSongDataStore dataStore;
  // TODO: just for debugging. remove.
  public static String songPath;

  static {
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    new Thread(() -> {
      ac.start();
    });
  }


  @FXML
  protected MainController mainController;

  public void setDataStore(SamplerSongDataStore dataStore) {
    AbstractController.dataStore = dataStore;
  }

  protected void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

  protected void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  protected ObjectMapper getMapper() {
    return mapper;
  }

  protected SamplerSongDataStore getDataStore() {
    return dataStore;
  }

  protected void error(final String title, final String header, final Throwable throwable) {
    error(title, header, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
  }

  protected void error(final String title, final String header, final String message) {
    final Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.showAndWait();
  }

  protected AudioContext getAudioContext() {
    return ac;
  }

  public String getSongPath() {
    return songPath;
  }
}
