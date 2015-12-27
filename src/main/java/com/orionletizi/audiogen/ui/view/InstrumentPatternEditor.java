package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.InstrumentPattern;
import com.orionletizi.sampler.sfz.Region;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.SamplePlayer;

import java.util.Set;

public class InstrumentPatternEditor extends Pane {
  private final GridPane grid;
  private AudioContext ac;

  public InstrumentPatternEditor(final AudioContext ac, final SfzSamplerProgram program, final InstrumentPattern pattern) {
    this.ac = ac;
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

    final Set<Region> regions = program.getRegionsByKey((byte) pattern.getSamplerNote());
    for (final Region region : regions) {
      row++;
      grid.add(new Label("Audio (hivel: " + region.getHivel() + "): "), 0, row);
      grid.add(new Label(region.getSample().getSimpleName()), 1, row);
      final Button playButton = new Button("Play");
      setPlayHandler(playButton, region.getSample());
      grid.add(playButton, 2, row);
    }

  }

  private void setPlayHandler(final Button button, final Sample sample) {
    final SamplePlayer player = new SamplePlayer(ac, sample);
    player.setKillOnEnd(false);
    player.pause(true);
    ac.out.addInput(player);

    info("Initial sample player state: " + player.isPaused());

    button.setOnAction(event -> {
      info("Play button event: " + event + "; player is paused: " + player.isPaused());
      togglePlayState(button, player);
    });
  }

  private void togglePlayState(final Button button, final SamplePlayer player) {
    if (player.isPaused()) {
      play(button, player);
    } else {
      stop(button, player);
    }
  }

  private void stop(Button button, SamplePlayer player) {
    info("In stop...");
    Platform.runLater(() -> button.setText("Play"));
    player.pause(true);
    player.setPosition(0);
  }

  private void play(final Button button, final SamplePlayer player) {
    info("In play...");
    Platform.runLater(() -> button.setText("Stop"));
    player.setPosition(0);
    player.pause(false);
    player.setEndListener(new Bead() {
      boolean open = true;

      @Override
      protected void messageReceived(Bead message) {
        if (open && player.equals(message)) {
          open = false;
          stop(button, player);
        }
      }
    });
  }

  private void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }
}
