package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.domain.InstrumentPattern;
import com.orionletizi.audiogen.ui.SongEditor;
import com.orionletizi.audiogen.ui.proxy.AccordionProxy;
import com.orionletizi.audiogen.ui.proxy.FileChooserProxy;
import com.orionletizi.audiogen.ui.proxy.TextFieldProxy;
import com.orionletizi.audiogen.ui.view.InstrumentPatternPane;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.TimeSignature;
import com.orionletizi.sampler.sfz.Region;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzParserException;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SongEditorController implements Initializable {

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

  private FileChooserProxy fileChooserProxy;
  private Map<Integer, InstrumentPattern> instrumentPatterns = new HashMap<>();

  private final ObjectMapper mapper = new ObjectMapper();

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
    chooseInstrumentPath.setOnAction((ActionEvent event) -> {
      info("Pressed choose instrument button!");
      chooseInstrument();
    });
    instrumentPathProxy = new TextFieldProxy(instrumentPath);
    //keyStackProxy = new AccordionProxy(keyStack);
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
        final List<InstrumentPattern> patterns = mapper.readValue(attributes, List.class);
        instrumentPatterns.clear();
        for (final InstrumentPattern pattern : patterns) {
          info("pattern: " + pattern);
          instrumentPatterns.put(pattern.getSamplerNote(), pattern);
        }
      }

      Platform.runLater(() -> {
        if (instrumentProgram != null) {
          instrumentPathProxy.setText(instrumentProgram.getAbsolutePath());
        }
        for (int i = 0; i < 128; i++) {
          final Set<Region> regions = program.getRegionsByKey((byte) i);
          if (regions != null) {
            //keyStackProxy.addTitledPane("Key " + i, new InstrumentKeyPane());
            final int key = i;
            final InstrumentPattern instrumentPattern = instrumentPatterns.get(i);
            final Label keyLabel = new Label("Key: " + i);
            keyLabel.setOnMouseEntered(event -> keyLabel.setUnderline(true));
            keyLabel.setOnMouseExited(event -> keyLabel.setUnderline(false));
            keyLabel.setOnMouseClicked(event -> {
              InstrumentPattern patt = instrumentPattern;
              if (patt == null) {
                patt = new InstrumentPattern(new TimeSignature(4, 4), 0.25f, key, new ChordStructure());
              }
              instrumentPatterns.put(key, patt);
              final InstrumentPatternPane instrumentPatternPane = new InstrumentPatternPane(patt);
              final ObservableList<Node> detail = this.keyDetail.getChildren();
              detail.clear();
              detail.add(instrumentPatternPane);
            });
            keyStack.getChildren().add(keyLabel);
          }
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SfzParserException e) {
      e.printStackTrace();
    }
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }
}

