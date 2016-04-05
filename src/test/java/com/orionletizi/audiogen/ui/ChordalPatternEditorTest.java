package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.domain.ChordStructurePattern;
import com.orionletizi.audiogen.ui.controller.ChordalPatternEditorController;
import com.orionletizi.audiogen.ui.view.ChordStructureEditor;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.Tempo;
import com.orionletizi.music.theory.TimeSignature;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChordalPatternEditorTest extends AbstractFXTester {
  @Override
  protected String getFxmlPath() {
    return "com/orionletizi/audiogen/ui/chordal-pattern-editor.fxml";
  }

  @Test
  public void testBasics() throws Exception {
    final ChordalPatternEditorController controller = super.loader.getController();
    final ChordStructurePattern pattern = mock(ChordStructurePattern.class);

    final Float division = 1.1F;
    when(pattern.getDivision()).thenReturn(division);

    // MOCK: Tempo
    final Tempo tempo = mock(Tempo.class);
    final Double bpm = 1.2d;
    when(tempo.getBPM()).thenReturn(bpm);
    when(pattern.getTempo()).thenReturn(tempo);

    // MOCK: Time signature
    final TimeSignature timeSignature = mock(TimeSignature.class);
    when(pattern.getTimeSignature()).thenReturn(timeSignature);
    final Integer beatsPerBar = 2;
    when(timeSignature.getBeatsPerBar()).thenReturn(beatsPerBar);
    final Integer beatUnit = 4;
    when(timeSignature.getBeatUnit()).thenReturn(beatUnit);

    // MOCK: ChordStructure
    final ChordStructure chordStructure = mock(ChordStructure.class);
    when(pattern.getStructure()).thenReturn(chordStructure);

    // TEST: The tempo starts empty
    final TextField tempoField = controller.getTempoField();
    assertEquals("", tempoField.getText());

    // TEST: The time signature starts empty
    final TextField numerator = controller.getTimeSignatureBeatsPerMeasureField();
    final TextField denominator = controller.getTimeSignatureBeatDivisionField();
    assertEquals("", numerator.getText());
    assertEquals("", denominator.getText());

    final CountDownLatch latch = new CountDownLatch(1);

    Platform.runLater(() -> {
      controller.setChordalPattern(pattern);
      latch.countDown();
    });
    // wait for the ui thread
    latch.await();

    // TEST: The tempo field reflects the tempo of the pattern
    assertEquals("" + bpm, tempoField.getText());

    // TEST: The time signature field reflects the time signature of the pattern
    assertEquals("" + beatsPerBar, numerator.getText());
    assertEquals("" + beatUnit, denominator.getText());


    // TEST: Click on the add segment button
    final ChordStructureEditor chordStructureEditor = controller.getChordStructureEditor();
    final Button addSegmentButton = chordStructureEditor.getAddSegmentButton();

    final CountDownLatch latch2 = new CountDownLatch(1);

    Platform.runLater(() -> {
      addSegmentButton.fire();
      latch2.countDown();
    });

    latch2.await();

    // TODO: Refactor and test the chord segment editing

  }

}
