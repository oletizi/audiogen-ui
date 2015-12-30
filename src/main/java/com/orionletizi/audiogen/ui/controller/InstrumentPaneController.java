package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orionletizi.audiogen.samplersong.domain.ChordalPattern;
import com.orionletizi.audiogen.ui.SongEditor;
import com.orionletizi.audiogen.ui.proxy.AccordionProxy;
import com.orionletizi.audiogen.ui.proxy.FileChooserProxy;
import com.orionletizi.audiogen.ui.proxy.TextFieldProxy;
import com.orionletizi.audiogen.ui.view.InstrumentPatternEditor;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// XXX: Using subclassing is probably the wrong way to go
public class InstrumentPaneController extends SongEditorController implements Initializable {

  private static Background selectedKeyBackground;
  private static Background unselectedKeyBackground;

  static {
    selectedKeyBackground = new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));
    unselectedKeyBackground = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
  }

  private Executor exec;



  @FXML
  private Button chooseInstrumentPath;

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
  private SfzSamplerProgram instrumentProgram;
  private EditableKey selectedKey;
  private final List<EditableKey> editableKeys = new ArrayList<>();


  public InstrumentPaneController() {
    // this is the real constructor used by JavaFX
  }

  /**
   * This constructor is specifically used for unit testing
   */
  public InstrumentPaneController(final Executor exec,
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



  public void setStage(Stage stage) {
    final EventHandler<KeyEvent> keyboardHandler = keyEvent -> {
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
      final File attributes = new File(instrumentProgramFile.getParentFile(), "attributes.json");

      instrumentProgram = new SfzSamplerProgram(new SfzParser(), instrumentProgramFile);

      if (attributes.isFile()) {
        instrumentPatterns.clear();

        final TypeFactory typeFactory = mapper.getTypeFactory();
        final MapType mapType = typeFactory.constructMapType(Map.class, Integer.class, ChordalPattern.class);
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
              instrumentPatterns.put(key, new ChordalPattern(Tempo.newTempoFromBPM(120), new TimeSignature(4, 4), 0.25f, key, new ChordStructure()));
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
    ChordalPattern patt = instrumentPatterns.get(key);
    info("Activating instrument pattern editor: " + key + ", pattern: " + patt);
    if (patt == null) {
      patt = new ChordalPattern(Tempo.newTempoFromBPM(120), new TimeSignature(4, 4), 0.25f, key, new ChordStructure());
      instrumentPatterns.put(key, patt);
    }
    if (patt.getTempo() == null) {
      patt.setTempo(Tempo.newTempoFromBPM(120));
    }
    final InstrumentPatternEditor instrumentPatternEditor = new InstrumentPatternEditor(ac, instrumentProgram, patt);
    final ObservableList<Node> detail = this.keyDetail.getChildren();
    detail.clear();
    detail.add(instrumentPatternEditor);
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

