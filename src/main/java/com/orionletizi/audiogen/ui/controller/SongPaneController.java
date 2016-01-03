package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.config.g2.DataStoreConfigG2;
import com.orionletizi.audiogen.samplersong.domain.BeatInstrument;
import com.orionletizi.audiogen.samplersong.domain.ChordalInstrument;
import com.orionletizi.audiogen.samplersong.domain.Song;
import com.orionletizi.audiogen.samplersong.gen.GenConfig;
import com.orionletizi.audiogen.samplersong.gen.PunkRockGeneration;
import com.orionletizi.audiogen.samplersong.io.SamplerSongDataStore;
import com.orionletizi.music.theory.Tempo;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.NonrealtimeIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class SongPaneController extends AbstractController {

  @FXML
  private Button openSongButton;
  @FXML
  private Button newSongButton;
  @FXML
  private TextField songNameField;
  @FXML
  private BeatInstrumentPaneController beatInstrumentPaneController;
  @FXML
  private Button addChordalInstrumentButton;
  @FXML
  private Button deleteChordalInstrumentButton;
  @FXML
  private ListView chordalInstrumentsListView;
  @FXML
  private Button saveSongButton;
  @FXML
  private TextField songPathDisplay;
  @FXML
  private TextField songPathField;

  @FXML
  private Button generateButton;
  @FXML
  private AudioPlayerController audioPlayerController;

  private Song song;

  private File songFile;
  private ObservableList<ChordalInstrument> selectedChordalInstruments;

  @Override
  @SuppressWarnings("unchecked")
  public void initialize(URL location, ResourceBundle resources) {
    openSongButton.setOnAction(event -> openSong());

    newSongButton.setOnAction(event -> newSong());

    songPathField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (validatePath(newValue)) {
        song.setSongPath(newValue);
      }
    });

    chordalInstrumentsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    selectedChordalInstruments = chordalInstrumentsListView.getSelectionModel().getSelectedItems();
    selectedChordalInstruments.addListener((ListChangeListener) c -> {
      deleteChordalInstrumentButton.setDisable(selectedChordalInstruments.isEmpty());
    });
    addChordalInstrumentButton.setOnAction(event -> addChordalInstrument());
    deleteChordalInstrumentButton.setOnAction(event -> deleteChordalInstrument());
    deleteChordalInstrumentButton.setDisable(false);

    saveSongButton.setDisable(true);
    saveSongButton.setOnAction(event -> saveSong());

    generateButton.setOnAction(event -> generate());

    setDisableEditor(true);
    info("Checking song path: " + getSongPath());
    if (getSongPath() != null) {
      try {
        loadSong(getDataStore().loadSong(getSongPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void deleteChordalInstrument() {
    for (ChordalInstrument instrument : selectedChordalInstruments) {
      song.getChordalInstruments().remove(instrument);
    }
    chordalInstrumentsListView.getItems().clear();
    chordalInstrumentsListView.getItems().addAll(song.getChordalInstruments());
  }

  private void addChordalInstrument() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose Chordal Instruments");
    final List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != null) {
      for (File file : files) {
        try {
          final ChordalInstrument instrument = getMapper().readValue(file, ChordalInstrument.class);
          song.getChordalInstruments().add(instrument);
          chordalInstrumentsListView.getItems().clear();
          chordalInstrumentsListView.getItems().addAll(song.getChordalInstruments());
        } catch (IOException e) {
          error("Error", "Error Loading Instrument", "file: " + file + "\n" + "error: " + e.getMessage());
        }
      }
    }
  }

  private void generate() {
    final PunkRockGeneration gen = new PunkRockGeneration(new AudioContext(new NonrealtimeIO()), dataStore, song);
    final GenConfig config = new GenConfig(Tempo.newTempoFromBPM(120), 10);
    final File generated;
    try {
      generated = gen.generate(config);
      audioPlayerController.setAudioFile(generated);
    } catch (Throwable t) {
      t.printStackTrace();
      error("Error", "Error Generating Audio", t);
    }
  }

  private void newSong() {
    loadSong(new Song());
  }


  private void setDisableEditor(boolean disable) {
    beatInstrumentPaneController.setDisableEditor(disable);
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

    BeatInstrument beatInstrument = song.getBeatInstrument();
    if (song.getBeatInstrument() == null) {
      beatInstrument = new BeatInstrument();
      song.setBeatInstrument(beatInstrument);
    }

    beatInstrumentPaneController.setBeatInstrument(beatInstrument);

    final Set<ChordalInstrument> chordalInstruments = song.getChordalInstruments();
    final ObservableList items = chordalInstrumentsListView.getItems();
    items.add(chordalInstruments);


    setDisableEditor(false);
  }

  public static void main(String[] args) throws Exception {
    final File home = new File(System.getProperty("user.home"));
    final File root = new File(home, "audiogen-data-test");
    final File localLib = new File(root, "data");
    final URL resourceLib = localLib.toURI().toURL();
    final File localWriteRoot = root;
    final SamplerSongDataStore dataStore = new SamplerSongDataStore(new DataStoreConfigG2(resourceLib, localLib, localWriteRoot));
    final SongPaneController controller = new SongPaneController();
    controller.setDataStore(dataStore);
    final Song song = dataStore.loadSong("asdf");
    System.out.println("Song: " + song);
    controller.song = song;
    controller.generate();
  }
}
