package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

public abstract class AbstractController implements Initializable {
  protected static final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
  protected static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    ac.start();
  }


  @FXML
  protected MainController mainController;

  protected void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

  protected void setMainController(MainController mainController) {
    this.mainController = mainController;
  }
}
