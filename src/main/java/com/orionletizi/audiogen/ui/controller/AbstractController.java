package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.io.SamplerSongDataStore;
import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

public abstract class AbstractController implements Initializable {

  private static final Logger logger = LoggerImpl.forClass(AbstractController.class);

  private static final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String CHORDAL_PATTERN_EDITOR_PATH = "com/orionletizi/audiogen/ui/chordal-pattern-editor.fxml";
  private static final String CHORDAL_MIDI_PATTERN_EDITOR_PATH = "com/orionletizi/audiogen/ui/chordal-midi-pattern-editor.fxml";
  private static final String CHORDAL_INSTRUMENT_PATTERN_EDITOR_PATH = "com/orionletizi/audiogen/ui/chordal-instrument-pattern-editor.fxml";
  public static SamplerSongDataStore dataStore;
  // TODO: just for debugging. remove.
  public static String songPath;

  static {
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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

  public AudioContext getAudioContext() {
    return ac;
  }

  public String getSongPath() {
    return songPath;
  }

  protected FXMLLoader getChordalPatternEditorLoader() {
    return getFXMLLoader(CHORDAL_PATTERN_EDITOR_PATH);
  }

  protected FXMLLoader getChordalMidiPatternEditorLoader() {
    return getFXMLLoader(CHORDAL_MIDI_PATTERN_EDITOR_PATH);
  }

  protected FXMLLoader getChordalInstrumentPatternEditorLoader() {
    return getFXMLLoader(CHORDAL_INSTRUMENT_PATTERN_EDITOR_PATH);
  }

  private FXMLLoader getFXMLLoader(final String path) {
    return new FXMLLoader(ClassLoader.getSystemResource(path));
  }

}
