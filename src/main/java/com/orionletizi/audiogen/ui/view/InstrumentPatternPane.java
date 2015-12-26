package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.InstrumentPattern;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class InstrumentPatternPane extends Pane {
  private final GridPane grid;
  private final InstrumentPattern pattern;

  public InstrumentPatternPane(final InstrumentPattern pattern) {
    this.pattern = pattern;
    this.grid = new GridPane();
    grid.setHgap(10);
    this.getChildren().add(grid);
    //this.getChildren().add(new Label("Note: " + pattern.getSamplerNote()));
    int row = 0;
    grid.add(new Label("Key number:"), 0, row);
    grid.add(new Label(pattern.getSamplerNote() + ""), 1, row);

    row++;
    grid.add(new Label("Time Signature:"), 0, row);
    grid.add(new TimeSignatureEditor(pattern.getTimeSignature()), 1, row);

    row++;
    grid.add(new Label("Division: "), 0, row);
    grid.add(new Label(pattern.getDivision() + ""), 1, row);

    row++;
    grid.add(new Label("Chord Structure:"), 0, row);
    grid.add(new Label(pattern.getChordStr().toString()), 1, row);

  }
}
