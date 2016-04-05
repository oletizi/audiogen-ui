package com.orionletizi.audiogen.ui.view;

import com.orionletizi.music.theory.Chord;
import com.orionletizi.music.theory.ChordStructure;
import com.orionletizi.music.theory.ChordStructureSegment;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChordStructureEditor extends Pane {

  private final GridPane grid;
  private final VBox vbox;
  private ChordStructure structure;
  private int row;
  private Button addSegmentButton;

  public ChordStructureEditor(final ChordStructure structure) {
    if (structure == null) throw new IllegalArgumentException("ChordStructure must not be null");
    grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    vbox = new VBox();
    vbox.getChildren().add(grid);
    vbox.setSpacing(10);
    this.getChildren().add(vbox);
    this.structure = structure;
    for (ChordStructureSegment segment : structure.getSegments()) {
      newSegmentEditor(segment);
    }
    addSegmentButton = new Button("Add Segment");
    addSegmentButton.setOnAction(event -> {
      final ChordStructureSegment segment = new ChordStructureSegment();
      structure.addSegment(segment);
      newSegmentEditor(segment);
    });
    vbox.getChildren().add(addSegmentButton);
  }

  private void newSegmentEditor(final ChordStructureSegment segment) {
    int col = 0;
    final Label chordLabel = new Label("Chord:");
    grid.add(chordLabel, col++, row);
    final String chordString = segment.getChord() == null ? "" : segment.getChord().getName();
    final TextField chordField = new TextField(chordString);
    chordField.textProperty().addListener(((observable1, oldValue1, newValue1) -> {
      if (!"".equals(newValue1)) {
        segment.setChord(new Chord(newValue1));
      }
    }));
    grid.add(chordField, col++, row);

    row++;
    col = 0;
    final Label beatCountLabel = new Label("Beat count:");
    grid.add(beatCountLabel, col++, row);
    final TextField beatCountField = new TextField(segment.getBeatCount() + "");
    beatCountField.textProperty().addListener(((observable, oldValue, newValue) -> {
      if (!"".equals(newValue)) {
        try {
          segment.setBeatCount(Double.parseDouble(newValue));
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }
    }));
    grid.add(beatCountField, col++, row);

    final Button removeButton = new Button("Remove");
    removeButton.setOnAction(event -> {
      structure.getSegments().remove(segment);
      grid.getChildren().removeAll(chordLabel, chordField, beatCountLabel, beatCountField, removeButton);
    });
    grid.add(removeButton, col++, row);

    row++;
  }

  public Button getAddSegmentButton() {
    return addSegmentButton;
  }
}
