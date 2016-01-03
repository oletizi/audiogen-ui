package com.orionletizi.audiogen.ui.controller;

import com.orionletizi.audiogen.samplersong.domain.BasicMidiPattern;
import com.orionletizi.audiogen.samplersong.domain.BeatInstrument;
import com.orionletizi.audiogen.samplersong.domain.MidiPattern;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BeatInstrumentPaneController extends AbstractController {

  private ObservableList beatPatterns;
  private BeatInstrument beatInstrument;
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

  public void addBeatPattern() {
    final FileChooser chooser = new FileChooser();
    chooser.setTitle("Open pattern");
    final List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != null) {
      for (File file : files) {
        final BasicMidiPattern beatPattern = new BasicMidiPattern();
        try {
          beatPattern.setMidiSource(file.toURI().toURL());
          beatInstrument.addBeatPattern(beatPattern);
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
      beatInstrument.getBeatPatterns().remove(view.pattern);
    }
  }

  public void setBeatInstrument(BeatInstrument beatInstrument) {
    final File beatProgramFile = beatInstrument.getSamplerProgramFile();
    beatInstrumentProgramDisplay.setText(beatProgramFile == null ? "" : beatProgramFile.getAbsolutePath());

    beatPatterns.clear();
    this.beatInstrument = beatInstrument;
    final List<BeatPatternView> viewList = new ArrayList<>();
    for (MidiPattern beatPattern : beatInstrument.getBeatPatterns()) {
      viewList.add(new BeatPatternView(beatPattern));
    }
    this.beatInstrument = beatInstrument;
    beatPatterns.addAll(viewList);
  }

  public void deleteBeatPatterns() {

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
      String rv = "";
      final URL midiSource = pattern.getMidiSource();
      if (midiSource != null) {
        final String file = midiSource.getFile();
        rv = file.substring(file.lastIndexOf('/'));
      }
      return rv;
    }
  }
}
