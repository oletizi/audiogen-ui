package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.domain.BeatInstrument;
import com.orionletizi.audiogen.samplersong.domain.BeatPattern;
import com.orionletizi.audiogen.samplersong.domain.Song;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class SongPaneController extends AbstractController {

  @FXML
  private Button openSongButton;
  @FXML
  private Button newSongButton;
  @FXML
  private TextField songNameField;
  @FXML
  private TextField beatInstrumentProgramDisplay;
  @FXML
  private Button chooseBeatProgramButton;
  @FXML
  private Button installBeatProgramButton;
  @FXML
  private Button addBeatPatternButton;
  @FXML
  private Button deleteBeatPatternButton;
  @FXML
  private Button saveSongButton;
  @FXML
  private TextField songPathDisplay;
  @FXML
  private TextField songPathField;
  @FXML
  private TableView beatPatternTable;

  private Song song;
  private BeatInstrument beatInstrument;
  private BeatInstrumentEditor beatInstrumentEditor;
  private SfzSamplerProgram beatProgram;
  private File songFile;

  @Override

  public void initialize(URL location, ResourceBundle resources) {
    openSongButton.setOnAction(event -> openSong());

    newSongButton.setOnAction(event -> newSong());

    songPathField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (validatePath(newValue)) {
        song.setSongPath(newValue);
      }
    });

    chooseBeatProgramButton.setOnAction(event -> chooseBeatProgram());

    installBeatProgramButton.setOnAction(event -> installBeatProgram());
    installBeatProgramButton.setDisable(true);

    addBeatPatternButton.setOnAction(event -> addBeatPattern());

    deleteBeatPatternButton.setOnAction(event -> deleteBeatPattern());

    saveSongButton.setDisable(true);
    saveSongButton.setOnAction(event -> saveSong());

    setDisableEditor(true);
  }

  private void newSong() {
    loadSong(new Song());
  }

  private void installBeatProgram() {
    final DirectoryChooser chooser = new DirectoryChooser();
    chooser.setInitialDirectory(dataStore.getLocalSongLibrary());
    chooser.setTitle("Choose Parent Directory");
    final File file = chooser.showDialog(null);
    if (file != null) {
      try {
        beatProgram.copyTo(file);
      } catch (IOException e) {
        e.printStackTrace();
        error("Error", "Error Installing Program", e.getMessage());
      }
    }
  }

  private void chooseBeatProgram() {
    final File file = new FileChooser().showOpenDialog(null);
    if (file != null) {
      try {
        // This is here just to validate that it loads. For now.
        beatProgram = new SfzSamplerProgram(new SfzParser(), file);
        beatInstrument.setSamplerProgramFile(file);
        beatInstrumentProgramDisplay.setText(file.getAbsolutePath());
        if (!dataStore.isLocalLibraryUrl(file.toURI().toURL())) {
          installBeatProgramButton.setDisable(false);
        } else {
          installBeatProgramButton.setDisable(true);
        }
      } catch (Exception e) {
        e.printStackTrace();
        error("Error", "Error Loading Program", e.getMessage());
      }
    }
  }

  private void setDisableEditor(boolean disable) {
    chooseBeatProgramButton.setDisable(disable);
    addBeatPatternButton.setDisable(disable);
    deleteBeatPatternButton.setDisable(disable);
    saveSongButton.setDisable(disable);
  }

  private boolean validatePath(String value) {
    info("Validate path: " + value);
    return value != null && !"".equals(value);
  }

  private void saveSong() {
    if (!validatePath(song.getSongPath())) {
      error("Error", "Set Song Path", "Please set the song path.");
    } else {
      try {
        // TODO: Figure out how to set the song file.
        loadSong(dataStore.save(song));
      } catch (IOException e) {
        error("Error", "Error Saving Song", e.getMessage());
      }
    }
  }

  private void deleteBeatPattern() {
    throw new RuntimeException("Implement me");
  }

  private void addBeatPattern() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Open pattern");
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      final BeatPattern beatPattern = new BeatPattern();
      try {
        beatPattern.setMidiSource(file.toURI().toURL());
        beatInstrument.addBeatPattern(beatPattern);
        final ObservableList items = beatPatternTable.getItems();
        items.add(beatPattern);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }

  private void openSong() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Open Song");
    chooser.setInitialDirectory(dataStore.getLocalSongLibrary());
    //chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("song.json", ".json"));
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      songFile = file;
      final ObjectMapper mapper = mainController.getMapper();
      try {
        loadSong(mapper.readValue(file, Song.class));
        songFile = file;
      } catch (IOException e) {
        error("Error", "Error Loading Song File", "Error loading song file: " + file);
      }
    }
  }

  private void loadSong(Song song) {
    this.song = song;
    final String path = validatePath(song.getSongPath()) ? song.getSongPath() : "";
    songPathDisplay.setText(path);
    songPathField.setText(path);


    if (song.getBeatInstrument() != null) {
      beatInstrument = song.getBeatInstrument();
      final File beatProgramFile = beatInstrument.getSamplerProgramFile();
      beatInstrumentProgramDisplay.setText(beatProgramFile == null ? "" : beatProgramFile.getAbsolutePath());
    } else {
      beatInstrument = new BeatInstrument();
      beatInstrumentProgramDisplay.setText("");
      song.setBeatInstrument(beatInstrument);
    }

    beatInstrumentEditor = new BeatInstrumentEditor(beatInstrument);

    setDisableEditor(false);
  }

  private class BeatInstrumentEditor {
    private final ObservableList tableItems;
    private BeatInstrument beatInstrument;

    private BeatInstrumentEditor(final BeatInstrument beatInstrument) {
      this.beatInstrument = beatInstrument;
      tableItems = beatPatternTable.getItems();
      tableItems.addAll(beatInstrument.getBeatPatterns());
    }
  }
}
