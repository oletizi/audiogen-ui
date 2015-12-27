package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.InstrumentPattern;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class InstrumentPatternEditor extends Pane {
  private final GridPane grid;
  private final InstrumentPattern pattern;

  public InstrumentPatternEditor(final InstrumentPattern pattern) {
    this.pattern = pattern;
    this.grid = new GridPane();
    grid.setHgap(10);
    this.getChildren().add(grid);
    int row = 0;
    grid.add(new Label("Key number:"), 0, row);
    grid.add(new Label(pattern.getSamplerNote() + ""), 1, row);

    row++;
    grid.add(new Label("Time Signature:"), 0, row);
    grid.add(new TimeSignatureEditor(pattern.getTimeSignature()), 1, row);

    row++;
    grid.add(new Label("Division: "), 0, row);
    final TextField divisionField = new TextField(pattern.getDivision() + "");
    divisionField.textProperty().addListener(((observable, oldValue, newValue) -> {
      if (!"".equals(newValue)) {
        try {
          final float division = Float.parseFloat(newValue);
          pattern.setDivision(division);
        } catch (NumberFormatException e) {
          e.printStackTrace();
          ;
        }
      }
    }));
    grid.add(divisionField, 1, row);

    row++;
    grid.add(new Label("Chord Structure:"), 0, row);
    grid.add(new Label(pattern.getChordStr().toString()), 1, row);

  }
}
