package com.orionletizi.audiogen.ui.view;

import com.orionletizi.music.theory.TimeSignature;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class TimeSignatureEditor extends Pane {
  private final HBox hbox;
  private final TimeSignature sig;

  public TimeSignatureEditor(final TimeSignature sig) {
    this.sig = sig;
    this.hbox = new HBox();
    hbox.setSpacing(10);
    this.getChildren().add(hbox);

    final ObservableList<Node> nodes = hbox.getChildren();

    final TextField beatsPerBarField = new TextField("" + sig.getBeatsPerBar());
    beatsPerBarField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        final int i = Integer.parseInt(newValue);
        sig.setBeatsPerBar(i);
      } catch (NumberFormatException e) {
        // TODO: Add some messaging.
        e.printStackTrace();
      }
    });

    final TextField beatUnitField = new TextField("" + sig.getBeatUnit());
    beatUnitField.textProperty().addListener(((observable, oldValue, newValue) -> {
      try {
        final int i = Integer.parseInt(newValue);
        sig.setBeatUnit(i);
      } catch (NumberFormatException e) {
        // TODO: Add some messaging.
        e.printStackTrace();
      }
    }));

    nodes.add(beatsPerBarField);
    nodes.add(new Label("/"));
    nodes.add(beatUnitField);
  }
}
