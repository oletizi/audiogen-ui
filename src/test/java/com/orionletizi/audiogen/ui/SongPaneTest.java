package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.domain.BeatInstrument;
import com.orionletizi.audiogen.domain.InstrumentVariants;
import com.orionletizi.audiogen.domain.MidiPattern;
import com.orionletizi.audiogen.domain.Song;
import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.controller.SongPaneController;
import com.orionletizi.audiogen.ui.view.AlertFactory;
import com.orionletizi.audiogen.ui.view.FChooser;
import com.orionletizi.audiogen.ui.view.IAlert;
import com.orionletizi.sampler.SamplerProgram;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.util.List;

import static com.orionletizi.util.Assertions.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SongPaneTest extends AbstractFXTester {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File localLib;
  private FChooser chooser;
  private final String songPath = "abc/def";
  private DataStore dataStore;
  private IAlert mockAlert;
  private SongPaneController controller;


  @Override
  public void start(final Stage stage) throws Exception {
    localLib = tmp.newFolder();
    dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());

    mockAlert = mock(IAlert.class);
    final AlertFactory alertFactory = type -> mockAlert;

    final String fxmlPath = getFxmlPath();
    final URL fxmlUrl = ClassLoader.getSystemResource(fxmlPath);
    final FXMLLoader loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();//FXMLLoader.load(fxmlUrl);

    controller = loader.getController();
    chooser = mock(FChooser.class);

    // Set up the mock infrastructure

    AbstractController.dataStore = dataStore;
    AbstractController.fchooser = chooser;
    AbstractController.alertFactory = alertFactory;

    new Thread(() -> controller.getAudioContext().start()).start();

    Scene scene = new Scene(root);
    stage.setTitle("Song Editor");
    stage.setScene(scene);
    stage.show();
  }

  @NotNull
  protected String getFxmlPath() {
    return "com/orionletizi/audiogen/ui/song-pane.fxml";
  }

  @Test
  public void testNewAndSave() throws Exception {
    final File expectedSongFile = startNewSong(songPath);

    saveSong();

    assertTrue(expectedSongFile.exists());
  }

  @Test
  public void testAddBeatInstrument() throws Exception {
    final File beatInstrumentFolder = new File(ClassLoader.getSystemResource("sfz/urbanage/urbanage.sfz").getFile());
    assertTrue(beatInstrumentFolder.exists());
    when(chooser.showOpenDialog()).thenReturn(beatInstrumentFolder);

    final File songFile = startNewSong(songPath);

    // TODO: Figure out how to test for button state
    //verifyThat(ADD_BEAT_INSTRUMENT_VARIANTS_BUTTON, is(disabled()));

    clickOn("#addBeatInstrumentVariantsButton");

    saveSong();

    assertFalse(songFile.exists());

    final Song song = dataStore.loadSong(songPath);
    final InstrumentVariants<BeatInstrument, MidiPattern> beatInstrumentVariants = song.getBeatInstrument();
    final List<BeatInstrument> beatInstruments = beatInstrumentVariants.getVariants();
    assertEquals(1, beatInstruments.size());
    final BeatInstrument beatInstrument = beatInstruments.get(0);
    final SamplerProgram samplerProgram = beatInstrument.getSamplerProgram(dataStore);
    assertNotNull(samplerProgram);
  }

  private void saveSong() {
    clickOn("#saveSongButton");
  }

  private File startNewSong(final String songPath) {
    final File expectedSongFile = new File(localLib, "songs/" + songPath + "/song.json");
    assertFalse(expectedSongFile.exists());

    clickOn("#newSongButton");
    clickOn("#songPathField");

    typeString(songPath);
    push(KeyCode.ENTER);
    return expectedSongFile;
  }

//  private void info(final String s) {
//    System.out.println(s);
//  }

}
