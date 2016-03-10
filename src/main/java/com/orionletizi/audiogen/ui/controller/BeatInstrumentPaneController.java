package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.domain.BasicMidiPattern;
import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.domain.InstrumentVariants;
import com.orionletizi.audiogen.domain.MidiPattern;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BeatInstrumentPaneController extends AbstractController {

  private ObservableList beatPatterns;
  private InstrumentVariants<BeatInstrument, MidiPattern> beatInstrument;
  private SfzSamplerProgram beatProgram;
  @FXML
  private TextField beatInstrumentProgramDisplay;
  @FXML
  private Button chooseBeatProgramButton;
  @FXML
  private Button installBeatProgramButton;
  @FXML
  private ListView beatPatternListView;
  @FXML
  private Button addBeatPatternButton;
  @FXML
  private Button deleteBeatPatternButton;
  private ObservableList<BeatPatternView> selectedBeatPatterns;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    beatPatternListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    deleteBeatPatternButton.setDisable(true);
    selectedBeatPatterns = beatPatternListView.getSelectionModel().getSelectedItems();
    selectedBeatPatterns.addListener((ListChangeListener) c -> deleteBeatPatternButton.setDisable(selectedBeatPatterns.isEmpty()));

    beatPatterns = beatPatternListView.getItems();

    chooseBeatProgramButton.setOnAction(event -> chooseBeatProgram());

    installBeatProgramButton.setOnAction(event -> installBeatProgram());
    installBeatProgramButton.setDisable(true);

    addBeatPatternButton.setOnAction(event -> addBeatPattern());

    deleteBeatPatternButton.setOnAction(event -> deleteBeatPattern());


  }


  private void installBeatProgram() {
    if (true) {
      throw new RuntimeException("Implement Me!");
    }
    final DirectoryChooser chooser = new DirectoryChooser();
    //chooser.setInitialDirectory(dataStore.getLocalSongLibrary());
//    chooser.setTitle("Choose Parent Directory");
//    final File file = chooser.showDialog(null);
//    if (file != null) {
//      try {
//        beatProgram.copyTo(file);
//      } catch (IOException e) {
//        e.printStackTrace();
//        error("Error", "Error Installing Program", e.getMessage());
//      }
//    }
  }

  private void chooseBeatProgram() {
    final File file = new FileChooser().showOpenDialog(null);
    if (file != null) {
      try {
        // This is here just to validate that it loads. For now.
        beatProgram = new SfzSamplerProgram(new SfzParser(), file);
        if (true) {
          throw new RuntimeException("FIXME!");
        }
        //beatInstrument.setSamplerProgramFile(file);
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

  public void addBeatPattern() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Open pattern");
    final List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != getChordalPatternEditorLoader()) {
      for (File file : files) {
        final MidiPattern beatPattern = new BasicMidiPattern();
        try {
          beatPattern.setSourceURL(file.toURI().toURL());
          beatInstrument.getPatterns().add(beatPattern);
          beatPatterns.add(new BeatPatternView(beatPattern));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void deleteBeatPattern() {
    assert !selectedBeatPatterns.isEmpty();
    info("deleteBeatPattern()...");
    info("selected: " + selectedBeatPatterns);
    for (BeatPatternView view : selectedBeatPatterns) {
      final boolean removed = beatPatterns.remove(view);
      assert removed;
      beatInstrument.getPatterns().remove(view.pattern);
    }
  }

  public void setBeatInstrument(InstrumentVariants<BeatInstrument, MidiPattern> beatInstrument) {

    if (true) {
      throw new RuntimeException("FIXME");
    }
    final File beatProgramFile = null;//beatInstrument.getSamplerProgramFile();
    beatInstrumentProgramDisplay.setText(beatProgramFile == null ? "" : beatProgramFile.getAbsolutePath());

    beatPatterns.clear();
    this.beatInstrument = beatInstrument;
    for (MidiPattern beatPattern : beatInstrument.getPatterns()) {
      beatPatterns.add(new BeatPatternView(beatPattern));
    }
    this.beatInstrument = beatInstrument;
  }

  public void deleteBeatPatterns() {
    throw new RuntimeException("Implement Me!");
  }

  public void setDisableEditor(boolean disable) {
    chooseBeatProgramButton.setDisable(disable);
    addBeatPatternButton.setDisable(disable);
    deleteBeatPatternButton.setDisable(disable);
  }

  private class BeatPatternView {
    private MidiPattern pattern;

    private BeatPatternView(final MidiPattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public String toString() {
      return pattern.getPath();
    }
  }
}
