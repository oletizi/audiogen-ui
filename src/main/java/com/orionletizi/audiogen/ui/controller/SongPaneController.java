package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.domain.*;
import com.orionletizi.audiogen.gen.GenConfig;
import com.orionletizi.audiogen.gen.PunkRockGeneration;
import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.player.AudioPlayer;
import com.orionletizi.audiogen.ui.view.InstrumentDisplay;
import com.orionletizi.audiogen.ui.view.MidiPatternDisplay;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.NonrealtimeIO;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;

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
  private Button newChordalInstrumentVariantsButton;
  @FXML
  private Button deleteChordalInstrumentVariantsButton;
  @FXML
  private ListView<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> chordalInstrumentVariantsListView;
  @FXML
  private ListView<ChordalInstrument> selectedChordalInstrumentVariantsListView;
  @FXML
  private ListView<ChordalMidiPattern> chordalInstrumentMidiPatternListView;
  @FXML
  private Button addChordalInstrumentButton;
  @FXML
  private Button deleteChordalInstrumentButton;
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

  private ObservableList<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> selectedChordalInstrumentVariants;

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
      if (validatePath(newValue)) {
        final ObservableList<InstrumentDisplay<BeatInstrument>> selectedItems = beatInstrumentVariantsListView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
          final InstrumentDisplay<BeatInstrument> selectedItem = selectedItems.get(0);
          final BeatInstrument instrument = selectedItem.getInstrument();
          instrument.setPath(newValue);
          installBeatInstrumentsButton.setDisable(false);
        }
      }
    });
    beatInstrumentVariantsListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<InstrumentDisplay<BeatInstrument>>) c -> {
      if (c.getList().isEmpty()) {
        disableBeatInstrumentEditor();
      } else {
        final BeatInstrument instrument = c.getList().get(0).getInstrument();
        deleteBeatInstrumentVariantsButton.setDisable(false);
        beatInstrumentPathField.setText(instrument.getPath());
        beatInstrumentPathField.setDisable(false);
        if (validatePath(instrument.getPath())) {
          installBeatInstrumentsButton.setDisable(false);
        }
      }
    });
    addBeatInstrumentVariantsButton.setOnAction(event -> addBeatInstrumentVariant());
    deleteBeatInstrumentVariantsButton.setOnAction(event -> deleteBeatInstruments());
    //installBeatInstrumentsButton.setOnAction(event -> installBeatInstruments());
    installBeatInstrumentsButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent event) {
        throw new RuntimeException("Deal with me!");
      }
    });

    beatInstrumentVariantsMidPatternsListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<MidiPatternDisplay>) c -> {
      deleteBeatInstrumentMidiPatternButton.setDisable(c.getList().isEmpty());
    });
    addBeatInstrumentMidiPatternButton.setOnAction(event -> addBeatInstrumentMidiPattern());
    deleteBeatInstrumentMidiPatternButton.setOnAction(event -> deleteBeatInstrumentMidiPattern());

    disableBeatInstrumentEditor();
    //
    // Set up chordal instrument controls
    //
    //chordalInstrumentsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    chordalInstrumentVariantsListView.setCellFactory(TextFieldListCell.forListView(new StringConverterWithFormat<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>>() {
      @Override
      public String toString(InstrumentVariants<ChordalInstrument, ChordalMidiPattern> object) {
        return object.getName();
      }

      @Override
      public InstrumentVariants<ChordalInstrument, ChordalMidiPattern> fromString(String string) {
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variants = chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0);
        variants.setName(string);
        return variants;
      }
    }));
    chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>>) c -> {
      if (!c.getList().isEmpty()) {
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variants = c.getList().get(0);
        final ObservableList<ChordalInstrument> items = selectedChordalInstrumentVariantsListView.getItems();
        items.clear();
        items.addAll(variants.getVariants());
      }
    });
    selectedChordalInstrumentVariants = chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems();
    selectedChordalInstrumentVariants.addListener((ListChangeListener) c -> {
      deleteChordalInstrumentVariantsButton.setDisable(selectedChordalInstrumentVariants.isEmpty());
      if (!selectedChordalInstrumentVariants.isEmpty()) {
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> instrument = getSelectedChordalInstrumentVariants();
        final ObservableList<ChordalMidiPattern> items = chordalInstrumentMidiPatternListView.getItems();
        items.clear();
        //items.addAll(instrument.getMidiPatterns());
        for (ChordalMidiPattern pattern : instrument.getPatterns()) {
          items.add(pattern);
        }
        disableChordalInstrumentEditor(false);
      } else {
        chordalInstrumentMidiPatternListView.getItems().clear();
        disableChordalInstrumentEditor(true);
      }
    });

    newChordalInstrumentVariantsButton.setOnAction(event -> addChordalInstrumentVariants());
    deleteChordalInstrumentVariantsButton.setOnAction(event -> deleteChordalInstrumentVariants());
    deleteChordalInstrumentVariantsButton.setDisable(false);

    addChordalInstrumentButton.setOnAction(event -> addChordalInstrument());
    deleteChordalInstrumentButton.setOnAction(event -> deleteChordalInstrument());

    disableChordalInstrumentEditor(true);

    chordalInstrumentMidiPatternListView.setCellFactory(new Callback<ListView<ChordalMidiPattern>, ListCell<ChordalMidiPattern>>() {
      @Override
      public ListCell<ChordalMidiPattern> call(ListView<ChordalMidiPattern> param) {

        return new ListCell<ChordalMidiPattern>() {
          @Override
          protected void updateItem(ChordalMidiPattern item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
              setText(item.getPath());
            }
          }
        };
      }
    });

    editChordalInstrumenMidiPatternButton.setOnAction(event -> editChordalInstrumentMidiPattern());
    addChordalInstrumentMidiPatternButton.setOnAction(event -> addChordalInstrumentMidiPattern());
    deleteChordalInstrumentMidiPatternButton.setOnAction(event -> deleteChordalInstrumentMidiPattern());

    saveSongButton.setDisable(true);
    saveSongButton.setOnAction(event -> saveSong());
    //installSongButton.setOnAction(event -> installSong());
    installSongButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent event) {
        throw new RuntimeException("Deal with me!");
      }
    });

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
    final ChordalInstrument instrument = selectedChordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0);
    final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> selectedVariants = getSelectedChordalInstrumentVariants();
    selectedVariants.getVariants().remove(instrument);
    selectedChordalInstrumentVariantsListView.getItems().remove(instrument);
  }

  private void addChordalInstrument() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Select Sampler Program");
    chooser.setInitialDirectory(dataStore.getLocalLibrary());
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      final ChordalInstrument instrument = new ChordalInstrument();
      try {
        instrument.setSourceURL(file.toURI().toURL());
      } catch (MalformedURLException e) {
        throw new RuntimeException("Seriously?", e);
      }
      instrument.setName(file.getParentFile().getName());
      if (true) {
        throw new RuntimeException("Instruments need a path!");
      }
      final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variants
          = chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0);
      variants.getVariants().add(instrument);
      selectedChordalInstrumentVariantsListView.getItems().add(instrument);
    }
  }

  private void disableChordalInstrumentEditor(boolean disable) {
    addChordalInstrumentButton.setDisable(disable);
    deleteChordalInstrumentButton.setDisable(disable);
  }

  private void disableBeatInstrumentEditor() {
    beatInstrumentPathField.setText("");
    beatInstrumentPathField.setDisable(true);
    deleteBeatInstrumentVariantsButton.setDisable(true);
    installBeatInstrumentsButton.setDisable(true);
    deleteBeatInstrumentMidiPatternButton.setDisable(true);
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
      try {
        beatInstrument.setSourceURL(file.toURI().toURL());
      } catch (MalformedURLException e) {
        throw new RuntimeException("Seriously?", e);
      }
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

  private void addBeatInstrumentMidiPattern() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Choose Midi Pattern");
    final List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != null) {
      for (File file : files) {
        final BasicMidiPattern pattern = new BasicMidiPattern();
        try {
          pattern.setSourceURL(file.toURI().toURL());
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

  private InstrumentVariants<ChordalInstrument, ChordalMidiPattern> getSelectedChordalInstrumentVariants() {
    return chordalInstrumentVariantsListView.getSelectionModel().getSelectedItems().get(0);
  }

  private ChordalMidiPattern getSelectedChordalMidiPattern() {
    return chordalInstrumentMidiPatternListView.getSelectionModel().getSelectedItems().get(0);
  }

  private Node getSelectedChordalMidiPatternNode() {
    return chordalInstrumentMidiPatternListView;
  }

  private void addChordalInstrumentMidiPattern() {
    final FileChooser chooser = new FileChooser();
    final File file = chooser.showOpenDialog(null);
    if (file != null) {
      try {
        final String path = null;
        if (path == null) {
          throw new RuntimeException("DEAL WITH MIDI PATTERN PATH!!!");
        }
        final ChordalMidiPattern midiPattern = new ChordalMidiPattern(Tempo.newTempoFromBPM(120), new TimeSignature(4, 4),
            1, new ChordStructure(), path, file.toURI().toURL());
        final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> instrument = getSelectedChordalInstrumentVariants();
        instrument.getPatterns().add(midiPattern);
        chordalInstrumentMidiPatternListView.getItems().add(midiPattern);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }

  private void editChordalInstrumentMidiPattern() {
    final InstrumentVariants variants = getSelectedChordalInstrumentVariants();
    final ChordalMidiPattern pattern = getSelectedChordalMidiPattern();
    final PopOver popOver = new PopOver(getSelectedChordalMidiPatternNode());

    final FXMLLoader loader = getChordalMidiPatternEditorLoader();

    try {
      popOver.setContentNode(loader.load());
      final ChordalMidiPatternEditorController controller = loader.<ChordalMidiPatternEditorController>getController();
      try {
        // all of the sampler programs should have identical regions, so it shouldn't matter which one we inspect
        controller.setChordalMidiPattern(variants.chooseInstrument().getSamplerProgram(dataStore), pattern);
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
    final ChordalMidiPattern pattern = getSelectedChordalMidiPattern();
    final InstrumentVariants instrument = getSelectedChordalInstrumentVariants();
    instrument.getPatterns().remove(pattern);
    chordalInstrumentMidiPatternListView.getItems().remove(pattern);
  }

  private void deleteChordalInstrumentVariants() {
    try {
      final Set<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> chordalInstruments = song.getChordalInstruments();
      for (InstrumentVariants<ChordalInstrument, ChordalMidiPattern> selected : selectedChordalInstrumentVariants) {
        chordalInstruments.remove(selected);
        chordalInstrumentVariantsListView.getItems().remove(selected);
      }
    } catch (IOException e) {
      e.printStackTrace();
      error("Error", "?!?!?!?", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void addChordalInstrumentVariants() {
    final InstrumentVariants<ChordalInstrument, ChordalMidiPattern> variants = new InstrumentVariants<>();
    try {
      song.getChordalInstruments().add(variants);
      chordalInstrumentVariantsListView.getItems().add(variants);
      chordalInstrumentVariantsListView.getSelectionModel().select(variants);
    } catch (IOException e) {
      error("Error", "WFT?!?", e);
    }
  }

  private void generate() {
    if (audioPlayerController != null) {
      audioPlayerController.kill();
    }
    final PunkRockGeneration gen = new PunkRockGeneration(new AudioContext(new NonrealtimeIO()), dataStore, song);
    final GenConfig config = new GenConfig(Tempo.newTempoFromBPM(120), 60 * 1000);
    info("Generating...");
    generateButton.setDisable(true);
    generateButton.setText("Generating...");
    new Thread(() -> {
      final File generated;
      try {
        generated = gen.generate(config);
        info("Done generating. generated: " + generated);
        Platform.runLater(() -> {
          generateButton.setText("Generate");
          generateButton.setDisable(false);
          try {
            audioPlayerController.setPlayer(new AudioPlayer(getAudioContext(), generated));
          } catch (IOException e) {
            error("Error", "Error Creating Audio Player", e);
          }
        });
      } catch (IOException e) {
        error("Error", "Error Generating Audio", e);
      }
    }).start();
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
    if (!validatePath(song.getPath())) {
      error("Error", "Set Song Path", "Please set the song path.");
    } else {
      try {
        // TODO: Figure out how to set the song file.
        // TODO: Figure out how to keep the song instruments modularized so that the instrument data doesn't get baked
        // into the song json output
        loadSong(dataStore.saveSong(song));
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
    final String path = validatePath(song.getPath()) ? song.getPath() : "";
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
    disableBeatInstrumentEditor();
    try {
      final Set<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> chordalInstruments = song.getChordalInstruments();
      final ObservableList<InstrumentVariants<ChordalInstrument, ChordalMidiPattern>> items = chordalInstrumentVariantsListView.getItems();
      items.clear();
      items.addAll(chordalInstruments);
    } catch (IOException e) {
      error("Error", "WTF!!!!", e);
    }
    setDisableEditor(false);
  }

  private class ChordalInstrumentVariantsCellFactory<InstrumentVariantsDisplay> implements Callback {

    @Override
    public Object call(Object param) {
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    final File home = new File(System.getProperty("user.home"));
    final File root = new File(home, "audiogen-data-test");
    final File localLib = new File(root, "data");
    final DataStore dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());
    final SongPaneController controller = new SongPaneController();
    controller.setDataStore(dataStore);
    final Song song = dataStore.loadSong("asdf");
    System.out.println("Song: " + song);
    controller.song = song;
    controller.generate();
  }
}
