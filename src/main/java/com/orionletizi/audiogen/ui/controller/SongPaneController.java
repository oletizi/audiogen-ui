package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.config.g2.DataStoreConfigG2;
import com.orionletizi.audiogen.samplersong.domain.*;
import com.orionletizi.audiogen.samplersong.gen.GenConfig;
import com.orionletizi.audiogen.samplersong.gen.PunkRockGeneration;
import com.orionletizi.audiogen.samplersong.io.SamplerSongDataStore;
import com.orionletizi.audiogen.ui.player.AudioPlayer;
import com.orionletizi.audiogen.ui.view.ChordalMidiPatternDisplay;
import com.orionletizi.audiogen.ui.view.InstrumentDisplay;
import com.orionletizi.audiogen.ui.view.InstrumentVariantsDisplay;
import com.orionletizi.audiogen.ui.view.MidiPatternDisplay;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.NonrealtimeIO;
import org.controlsfx.control.PopOver;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class SongPaneController extends AbstractController {
  //private static final URL chordalMidiPatternEditorUrl = ClassLoader.getSystemResource("");

  @FXML
  private Button openSongButton;
  @FXML
  private Button newSongButton;
  @FXML
  private TextField songNameField;
  @FXML
  private TextField beatInstrumentPathField;
  @FXML
  private ListView<InstrumentDisplay<BeatInstrument>> beatInstrumentVariantsListView;
  @FXML
  private Button addBeatInstrumentVariantsButton;
  @FXML
  private Button deleteBeatInstrumentVariantsButton;
  @FXML
  private Button installBeatInstrumentsButton;
  @FXML
  private ListView<MidiPatternDisplay> beatInstrumentVariantsMidPatternsListView;
  @FXML
  private Button addBeatInstrumentMidiPatternButton;
  @FXML
  private Button deleteBeatInstrumentMidiPatternButton;
  @FXML
  private Button installBeatInstrumentMidiPatternButton;
  @FXML
  private Button addChordalInstrumentVariantsButton;
  @FXML
  private Button deleteChordalInstrumentVariantsButton;
  @FXML
  private ListView<InstrumentVariantsDisplay<ChordalInstrument, ChordalMidiPattern>> chordalInstrumentVariantsListView;
  @FXML
  private ListView<ChordalMidiPatternDisplay> chordalInstrumentMidiPatternList;
  @FXML
  private Button addChordalInstrumentMidiPatternButton;
  @FXML
  private Button editChordalInstrumenMidiPatternButton;
  @FXML
  private Button deleteChordalInstrumentMidiPatternButton;
  @FXML
  private Button saveSongButton;
  @FXML
  private Button installSongButton;
  @FXML
  private TextField songPathDisplay;
  @FXML
  private TextField songPathField;

  @FXML
  private Button generateButton;
  @FXML
  private PlayerController audioPlayerController;

  private Song song;

  private ObservableList<InstrumentVariantsDisplay<ChordalInstrument, ChordalMidiPattern>> selectedChordalInstrumentVariants;

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

    //
    // Set up beat instrument controls
    //
    beatInstrumentPathField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !"".equals(newValue)) {
        final ObservableList<InstrumentDisplay<BeatInstrument>> selectedItems = beatInstrumentVariantsListView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
          final InstrumentDisplay<BeatInstrument> selectedItem = selectedItems.get(0);
          final BeatInstrument instrument = selectedItem.getInstrument();
          instrument.setPath(newValue);
        }
      }
    });
    beatInstrumentVariantsListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<InstrumentDisplay<BeatInstrument>>) c -> {
      if (c.getList().isEmpty()) {
        deleteBeatInstrumentVariantsButton.setDisable(true);
        installBeatInstrumentsButton.setDisable(true);
        beatInstrumentPathField.setText("");
        beatInstrumentPathField.setDisable(true);
      } else {
        final BeatInstrument instrument = c.getList().get(0).getInstrument();
        deleteBeatInstrumentVariantsButton.setDisable(false);
        installBeatInstrumentsButton.setDisable(false);
        beatInstrumentPathField.setText(instrument.getPath());
      }
    });
    addBeatInstrumentVariantsButton.setOnAction(event -> addBeatInstrumentVariant());
    deleteBeatInstrumentVariantsButton.setOnAction(event -> deleteBeatInstruments());
    installBeatInstrumentsButton.setOnAction(event -> installBeatInstruments());

    addBeatInstrumentMidiPatternButton.setOnAction(event -> addBeatInstrumentMidiPattern());
    deleteBeatInstrumentMidiPatternButton.setOnAction(event -> deleteBeatInstrumentMidiPattern());
    //
    // Set up chordal instrument controls
    //
    //chordalInstrumentsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    selectedChordalInstrumentVariants = chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems();
    selectedChordalInstrumentVariants.addListener((ListChangeListener) c -> {
      deleteChordalInstrumentVariantsButton.setDisable(selectedChordalInstrumentVariants.isEmpty());
      if (!selectedChordalInstrumentVariants.isEmpty()) {
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> instrument = getSelectedChordalInstrument();
        final ObservableList<ChordalMidiPatternDisplay> items = chordalInstrumentMidiPatternList.getItems();
        items.clear();
        //items.addAll(instrument.getMidiPatterns());
        for (ChordalMidiPattern pattern : instrument.getPatterns()) {
          items.add(new ChordalMidiPatternDisplay(pattern));
        }

      } else {
        chordalInstrumentMidiPatternList.getItems().clear();
      }
    });

    addChordalInstrumentVariantsButton.setOnAction(event -> addChordalInstrumentVariants());
    deleteChordalInstrumentVariantsButton.setOnAction(event -> deleteChordalInstrumentVariants());
    deleteChordalInstrumentVariantsButton.setDisable(false);

    editChordalInstrumenMidiPatternButton.setOnAction(event -> editChordalInstrumentMidiPattern());
    addChordalInstrumentMidiPatternButton.setOnAction(event -> addChordalInstrumentMidiPattern());
    deleteChordalInstrumentMidiPatternButton.setOnAction(event -> deleteChordalInstrumentMidiPattern());

    saveSongButton.setDisable(true);
    saveSongButton.setOnAction(event -> saveSong());
    installSongButton.setOnAction(event -> installSong());

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

  private void installSong() {
    try {
      loadSong(dataStore.installSong(song));
    } catch (IOException e) {
      error("Error", "Error Installing Song", e);
    }
  }

  //
  // Beat Instruments & Patterns
  //

  private void addBeatInstrumentVariant() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose a Beat Instrument...");
    chooser.setInitialDirectory(dataStore.getLocalLibrary());
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      // Create new beat instrument & set it on the song
      final BeatInstrument beatInstrument = new BeatInstrument();
      beatInstrument.setSamplerProgramFile(file);
      beatInstrument.setName(file.getParentFile().getName());
      final InstrumentVariants<BeatInstrument, MidiPattern> variants = song.getBeatInstrument();
      variants.getVariants().add(beatInstrument);

      // Update the beat instrument list view
      beatInstrumentVariantsListView.getItems().add(new InstrumentDisplay<>(beatInstrument));
    }
  }

  private void deleteBeatInstruments() {
    final ObservableList<InstrumentDisplay<BeatInstrument>> selectedItems = beatInstrumentVariantsListView.getSelectionModel().getSelectedItems();
    for (InstrumentDisplay<BeatInstrument> selectedItem : selectedItems) {
      song.getBeatInstrument().getVariants().remove(selectedItem.getInstrument());
    }
    beatInstrumentVariantsListView.getItems().removeAll(selectedItems);
  }


  private void installBeatInstruments() {
    if (!beatInstrumentVariantsListView.getSelectionModel().getSelectedItems().isEmpty()) {
      final InstrumentDisplay<BeatInstrument> selectedItem = beatInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0);
      final String path = selectedItem.getInstrument().getPath();
      if (path == null || "".equals(path)) {
        error("Error", "Please Set Instrument Path", "Please enter a valid instrument path.");
      } else {
        try {
          dataStore.installSamplerProgram(path, selectedItem.getInstrument().getSamplerProgram());
        } catch (IOException e) {
          error("Error", "WTF!?", e);
        }
      }
    }
  }


  private void addBeatInstrumentMidiPattern() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose Midi Pattern");
    final List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != null) {
      for (File file : files) {
        final BasicMidiPattern pattern = new BasicMidiPattern();
        try {
          pattern.setMidiSource(file.toURI().toURL());
          song.getBeatInstrument().getPatterns().add(pattern);
          beatInstrumentVariantsMidPatternsListView.getItems().add(new MidiPatternDisplay(pattern));
        } catch (MalformedURLException e) {
          error("Error", "MalformedURLException", e);
        }
      }

    }
  }

  private void deleteBeatInstrumentMidiPattern() {
    final ObservableList<MidiPatternDisplay> selectedItems = beatInstrumentVariantsMidPatternsListView.getSelectionModel().getSelectedItems();
    if (!selectedItems.isEmpty()) {
      for (MidiPatternDisplay selectedItem : selectedItems) {
        song.getBeatInstrument().getPatterns().remove(selectedItem.getPattern());
      }
      beatInstrumentVariantsMidPatternsListView.getItems().removeAll(selectedItems);
    }
  }

  //
  // Chordal Instruments & Patterns
  //

  private InstrumentVariants<ChordalInstrument, ChordalMidiPattern> getSelectedChordalInstrument() {
    return chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0).getVariants();
  }

  private ChordalMidiPatternDisplay getSelectedChordalMidiPattern() {
    return chordalInstrumentMidiPatternList.getSelectionModel().getSelectedItems().get(0);
  }

  private Node getSelectedChordalMidiPatternNode() {
    return chordalInstrumentMidiPatternList;
  }

  private void addChordalInstrumentMidiPattern() {
    final FileChooser chooser = new FileChooser();
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      try {
        final ChordalMidiPattern midiPattern = new ChordalMidiPattern(Tempo.newTempoFromBPM(120), new TimeSignature(4, 4),
            1, new ChordStructure(), file.toURI().toURL());
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> instrument = getSelectedChordalInstrument();
        instrument.getPatterns().add(midiPattern);
        chordalInstrumentMidiPatternList.getItems().add(new ChordalMidiPatternDisplay(midiPattern));
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }

  private void editChordalInstrumentMidiPattern() {
    final InstrumentVariants variants = getSelectedChordalInstrument();
    final ChordalMidiPatternDisplay patternDisplay = getSelectedChordalMidiPattern();
    final PopOver popOver = new PopOver(getSelectedChordalMidiPatternNode());

    final FXMLLoader loader = getChordalMidiPatternEditorLoader();

    try {
      popOver.setContentNode(loader.load());
      final ChordalMidiPatternEditorController controller = loader.<ChordalMidiPatternEditorController>getController();
      try {
        // all of the sampler programs should have identical regions, so it shouldn't matter which one we inspect
        controller.setChordalMidiPattern(variants.chooseInstrument().getSamplerProgram(), patternDisplay.getPattern());
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
        error("Error", "Error Setting MidiPatternEditor", e);
      }
      popOver.show(getSelectedChordalMidiPatternNode());
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "Error Loading Pattern Editor", e);
    }
  }

  private void deleteChordalInstrumentMidiPattern() {
    final ChordalMidiPatternDisplay midiPatternDisplay = getSelectedChordalMidiPattern();
    final InstrumentVariants instrument = getSelectedChordalInstrument();
    instrument.getPatterns().remove(midiPatternDisplay.getPattern());
    chordalInstrumentMidiPatternList.getItems().remove(midiPatternDisplay);
  }


  private void deleteChordalInstrumentVariants() {
    try {
      final Set<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> chordalInstruments = song.getChordalInstruments();
      for (InstrumentVariantsDisplay<ChordalInstrument, ChordalMidiPattern> selected : selectedChordalInstrumentVariants) {
        chordalInstruments.remove(selected.getVariants());
      }
      chordalInstrumentVariantsListView.getItems().clear();
      for (InstrumentVariants<ChordalInstrument, ChordalMidiPattern> chordalInstrument : chordalInstruments) {
        chordalInstrumentVariantsListView.getItems().add(new InstrumentVariantsDisplay<>(chordalInstrument));
      }
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "?!?!?!?", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void addChordalInstrumentVariants() {
    final DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("Choose Chordal Instruments");
    final File dir = chooser.showDialog(null);
    if (dir != null) {
      final File file = new File(dir, "instrument-attributes.json");
      try {
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variants = getMapper().readValue(file, InstrumentVariants.class);
        song.getChordalInstruments().add(variants);
        chordalInstrumentVariantsListView.getItems().clear();
        for (InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variant : song.getChordalInstruments()) {
          chordalInstrumentVariantsListView.getItems().add(new InstrumentVariantsDisplay<>(variant));
        }

      } catch (IOException e) {
        error("Error", "Error Loading Instrument", "file: " + file + "\n" + "error: " + e.getMessage());
      }
    }
  }

  private void generate() {
    final PunkRockGeneration gen = new PunkRockGeneration(new AudioContext(new NonrealtimeIO()), dataStore, song);
    final GenConfig config = new GenConfig(Tempo.newTempoFromBPM(120), 10);
    final File generated;
    try {
      generated = gen.generate(config);
      audioPlayerController.setPlayer(new AudioPlayer(getAudioContext(), generated));
    } catch (Throwable t) {
      t.printStackTrace();
      error("Error", "Error Generating Audio", t);
    }
  }

  private void newSong() {
    loadSong(new Song());
  }


  private void setDisableEditor(boolean disable) {
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
        // TODO: Figure out how to keep the song instruments modularized so that the instrument data doesn't get baked
        // into the song json output
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
      final ObjectMapper mapper = mainController.getMapper();
      try {
        loadSong(mapper.readValue(file, Song.class));
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

    InstrumentVariants<BeatInstrument, MidiPattern> beatInstrument = song.getBeatInstrument();
    if (beatInstrument == null) {
      beatInstrument = new InstrumentVariants<>();
      song.setBeatInstrument(beatInstrument);
    }

    beatInstrumentVariantsListView.getItems().clear();
    for (BeatInstrument instrument : beatInstrument.getVariants()) {
      beatInstrumentVariantsListView.getItems().add(new InstrumentDisplay<>(instrument));
    }
    beatInstrumentVariantsMidPatternsListView.getItems().clear();
    for (MidiPattern midiPattern : beatInstrument.getPatterns()) {
      beatInstrumentVariantsMidPatternsListView.getItems().add(new MidiPatternDisplay(midiPattern));
    }

    try {
      final Set<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> chordalInstruments = song.getChordalInstruments();
      final ObservableList<InstrumentVariantsDisplay<ChordalInstrument, ChordalMidiPattern>> items = chordalInstrumentVariantsListView.getItems();
      items.clear();
      for (InstrumentVariants<ChordalInstrument, ChordalMidiPattern> chordalInstrument : chordalInstruments) {
        items.add(new InstrumentVariantsDisplay<>(chordalInstrument));
      }
    } catch (IOException e) {
      error("Error", "WTF!!!!", e);
    }
    setDisableEditor(false);
  }

  public static void main(String[] args) throws Exception {
    final File home = new File(System.getProperty("user.home"));
    final File root = new File(home, "audiogen-data-test");
    final File localLib = new File(root, "data");
    final URL resourceLib = localLib.toURI().toURL();
    final SamplerSongDataStore dataStore = new SamplerSongDataStore(new DataStoreConfigG2(resourceLib, localLib, root));
    final SongPaneController controller = new SongPaneController();
    controller.setDataStore(dataStore);
    final Song song = dataStore.loadSong("asdf");
    System.out.println("Song: " + song);
    controller.song = song;
    controller.generate();
  }
}
