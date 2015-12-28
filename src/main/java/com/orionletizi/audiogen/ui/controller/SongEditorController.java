package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orionletizi.audiogen.samplersong.domain.InstrumentPattern;
import com.orionletizi.audiogen.ui.SongEditor;
import com.orionletizi.audiogen.ui.proxy.AccordionProxy;
import com.orionletizi.audiogen.ui.proxy.FileChooserProxy;
import com.orionletizi.audiogen.ui.proxy.TextFieldProxy;
import com.orionletizi.audiogen.ui.view.InstrumentPatternEditor;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.TimeSignature;
import com.orionletizi.sampler.sfz.Region;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzParserException;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// TODO: Reimplement all the UI stuff as SceneBuilder files backed by controllers. The current approach is a grody hack
// just to get some stuff working.
//
public class SongEditorController implements Initializable {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final AudioContext ac = new AudioContext(new JavaSoundAudioIO());
  private static Background selectedKeyBackground;
  private static Background unselectedKeyBackground;

  static {
    selectedKeyBackground = new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));
    unselectedKeyBackground = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));

    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    ac.start();
  }

  private Executor exec;

  @FXML
  private MenuBar menuBar;

  @FXML
  private Button chooseInstrumentPath;
  private File instrumentProgramFile;

  @FXML
  private TextField instrumentPath;
  private TextFieldProxy instrumentPathProxy;

  @FXML
  private VBox keyStack;

  @FXML
  private VBox keyDetail;

  @FXML
  private Button saveInstrumentPatternButton;

  private FileChooserProxy fileChooserProxy;
  private Map<Integer, InstrumentPattern> instrumentPatterns = new TreeMap<>();
  private SfzSamplerProgram instrumentProgram;
  private EditableKey selectedKey;
  private final List<EditableKey> editableKeys = new ArrayList<>();


  public SongEditorController() {
    // this is the real constructor used by JavaFX
  }

  /**
   * This constructor is specifically used for unit testing
   */
  public SongEditorController(final Executor exec,
                              final FileChooserProxy fileChooserProxy,
                              final TextFieldProxy instrumentPathProxy,
                              final AccordionProxy keyStackProxy) {
    this.exec = exec;
    this.fileChooserProxy = fileChooserProxy;
    this.instrumentPathProxy = instrumentPathProxy;
  }

  public void initialize(URL location, ResourceBundle resources) {
    exec = new ThreadPoolExecutor(1, 1, 10 * 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    // XXX: Hack.
    if (SongEditor.programFile != null) {
      setInstrumentProgramFile(SongEditor.programFile);
    }

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


    fileChooserProxy = new FileChooserProxy(null);
    chooseInstrumentPath.setOnAction((event) -> {
      info("Pressed choose instrument button!");
      chooseInstrument();
    });
    instrumentPathProxy = new TextFieldProxy(instrumentPath);

    saveInstrumentPatternButton.setOnAction(event -> {
      info("Saving instrument pattern to file...");
      save();
    });

  }

  private void quit() {
    // TODO: implement a save or cancel dialog
    save();
    ac.stop();
    System.exit(0);
  }

  private void save() {
    try {
      info("Saving...");
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File(instrumentProgramFile.getParentFile(), "attributes.json"), instrumentPatterns);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setStage(Stage stage) {
    final EventHandler<KeyEvent> keyboardHandler = keyEvent -> {
      info("Key event: " + keyEvent);
      // XXX: TODO: This totally needs to be refactored.
      if (keyEvent.getCode() == KeyCode.UP) {
        if (selectedKey == null) {
          selectedKey = editableKeys.get(0);
        }
        final int index = Math.max(0, selectedKey.index - 1);
        selectedKey.keyLabel.setBackground(unselectedKeyBackground);
        selectedKey = editableKeys.get(index);
        selectedKey.keyLabel.setBackground(selectedKeyBackground);
        activateInstrumentPatternEditor(selectedKey.key);
      } else if (keyEvent.getCode() == KeyCode.DOWN) {
        if (selectedKey == null) {
          selectedKey = editableKeys.get(0);
        }
        final int index = Math.min(editableKeys.size() - 1, selectedKey.index + 1);
        selectedKey.keyLabel.setBackground(unselectedKeyBackground);
        selectedKey = editableKeys.get(index);
        selectedKey.keyLabel.setBackground(selectedKeyBackground);
        activateInstrumentPatternEditor(selectedKey.key);
      }
    };
    stage.addEventHandler(KeyEvent.ANY, keyboardHandler);
  }


  public void chooseInstrument() {
    // TODO: restrict selection to .sfz files
    File chosenFile = fileChooserProxy.getFile();
    exec.execute(() -> {
      setInstrumentProgramFile(chosenFile);
    });
  }

  private void setInstrumentProgramFile(final File file) {
    try {
      instrumentProgramFile = file;
      final URL programResource = instrumentProgramFile.toURI().toURL();
      final File samples = instrumentProgramFile.getParentFile();
      final File attributes = new File(instrumentProgramFile.getParentFile(), "attributes.json");
      info("Program resource: " + programResource);
      info("samples: " + samples);

      instrumentProgram = new SfzSamplerProgram(new SfzParser(), programResource, samples);

      if (attributes.isFile()) {
        instrumentPatterns.clear();

        final TypeFactory typeFactory = mapper.getTypeFactory();
        final MapType mapType = typeFactory.constructMapType(Map.class, Integer.class, InstrumentPattern.class);
        instrumentPatterns.putAll(mapper.readValue(attributes, mapType));
      }
      Platform.runLater(() -> {
        if (instrumentProgramFile != null) {
          instrumentPathProxy.setText(instrumentProgramFile.getAbsolutePath());
        }
        boolean initialEditorActivated = false;
        editableKeys.clear();
        for (int i = 0; i < 128; i++) {
          // XXX: It's probably better to get the key set or entry set rather than
          // iterate through all possible keys.
          final Set<Region> regions = instrumentProgram.getRegionsByKey((byte) i);
          if (regions != null) {
            final int key = i;
            if (!instrumentPatterns.containsKey(key)) {
              instrumentPatterns.put(key, new InstrumentPattern(new TimeSignature(4, 4), 0.25f, key, new ChordStructure()));
            }

            final Label keyLabel = new Label("Key: " + i);
            final EditableKey editableKey = new EditableKey(editableKeys.size(), key, keyLabel);
            editableKeys.add(editableKey);
            keyLabel.setOnMouseEntered(event -> keyLabel.setUnderline(true));
            keyLabel.setOnMouseExited(event -> keyLabel.setUnderline(false));
            keyLabel.setOnMouseClicked(event -> {
              //keyLabel.getBackground().getFills().add(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));
              if (selectedKey != null) {
                selectedKey.keyLabel.setBackground(unselectedKeyBackground);
              }
              selectedKey = editableKey;
              keyLabel.setBackground(selectedKeyBackground);
              activateInstrumentPatternEditor(key);
            });
            keyStack.getChildren().add(keyLabel);

            if (!initialEditorActivated) {
              // activate the instrument pattern editor for the first defined key
              selectedKey = editableKey;
              selectedKey.keyLabel.setBackground(selectedKeyBackground);
              activateInstrumentPatternEditor(i);
              initialEditorActivated = true;
            }
          }
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SfzParserException e) {
      e.printStackTrace();
    }
  }

  private void activateInstrumentPatternEditor(int key) {
    InstrumentPattern patt = instrumentPatterns.get(key);
    info("Activating instrument pattern editor: " + key + ", pattern: " + patt);
    if (patt == null) {
      patt = new InstrumentPattern(new TimeSignature(4, 4), 0.25f, key, new ChordStructure());
      instrumentPatterns.put(key, patt);
    }
    final InstrumentPatternEditor instrumentPatternEditor = new InstrumentPatternEditor(ac, instrumentProgram, patt);
    final ObservableList<Node> detail = this.keyDetail.getChildren();
    detail.clear();
    detail.add(instrumentPatternEditor);
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }

  private class EditableKey {
    private int index;
    private final int key;
    private final Label keyLabel;

    private EditableKey(final int index, final int key, final Label keyLabel) {
      this.index = index;
      this.key = key;
      this.keyLabel = keyLabel;
    }
  }

}

