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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SongEditorController implements Initializable {

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  private Window window;
  private Executor exec;

  @FXML
  private Button chooseInstrumentPath;
  private File instrumentProgram;

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
      setInstrumentProgram(SongEditor.programFile);
    }
    fileChooserProxy = new FileChooserProxy(window);
    chooseInstrumentPath.setOnAction((event) -> {
      info("Pressed choose instrument button!");
      chooseInstrument();
    });
    instrumentPathProxy = new TextFieldProxy(instrumentPath);

    saveInstrumentPatternButton.setOnAction(event -> {
      info("Saving instrument pattern to file...");
      try {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(instrumentProgram.getParentFile(), "attributes.json"), instrumentPatterns);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }


  public void chooseInstrument() {
    // TODO: restrict selection to .sfz files
    File chosenFile = fileChooserProxy.getFile();
    exec.execute(() -> {
      setInstrumentProgram(chosenFile);
    });
  }

  private void setInstrumentProgram(final File file) {
    try {
      instrumentProgram = file;
      final URL programResource = instrumentProgram.toURI().toURL();
      final File samples = instrumentProgram.getParentFile();
      final File attributes = new File(instrumentProgram.getParentFile(), "attributes.json");
      info("Program resource: " + programResource);
      info("samples: " + samples);

      final SfzSamplerProgram program = new SfzSamplerProgram(new SfzParser(), programResource, samples);

      if (attributes.isFile()) {
        instrumentPatterns.clear();

        final TypeFactory typeFactory = mapper.getTypeFactory();
        final MapType mapType = typeFactory.constructMapType(Map.class, Integer.class, InstrumentPattern.class);
        instrumentPatterns.putAll(mapper.readValue(attributes, mapType));
      }
      Platform.runLater(() -> {
        if (instrumentProgram != null) {
          instrumentPathProxy.setText(instrumentProgram.getAbsolutePath());
        }
        boolean initialEditorActivated = false;
        for (int i = 0; i < 128; i++) {
          // XXX: It's probably better to get the key set or entry set rather than
          // iterate through all possible keys.
          final Set<Region> regions = program.getRegionsByKey((byte) i);
          if (regions != null) {
            final int key = i;
            if (!instrumentPatterns.containsKey(key)) {
              instrumentPatterns.put(key, new InstrumentPattern(new TimeSignature(4, 4), 0.25f, key, new ChordStructure()));
            }

            final Label keyLabel = new Label("Key: " + i);
            keyLabel.setOnMouseEntered(event -> keyLabel.setUnderline(true));
            keyLabel.setOnMouseExited(event -> keyLabel.setUnderline(false));
            keyLabel.setOnMouseClicked(event -> {
              activateInstrumentPatternEditor(key);
            });
            keyStack.getChildren().add(keyLabel);

            if (!initialEditorActivated) {
              // activate the instrument pattern editor for the first defined key
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
    final InstrumentPatternEditor instrumentPatternEditor = new InstrumentPatternEditor(patt);
    final ObservableList<Node> detail = this.keyDetail.getChildren();
    detail.clear();
    detail.add(instrumentPatternEditor);
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }
}

