package com.orionletizi.audiogen.ui.player;

import com.orionletizi.sampler.Sampler;
import com.orionletizi.sampler.SamplerProgram;
import com.orionletizi.sequencer.Sequencer;
import net.beadsproject.beads.core.AudioContext;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SequencerPlayer implements Player {

  private final Sequencer sequencer;
  private final AudioContext ac;
  private URL midiSource;
  private boolean paused = true;

  public SequencerPlayer(final AudioContext ac, final SamplerProgram program, final URL midiSource) throws InvalidMidiDataException,
      IOException {
    this.midiSource = midiSource;
    final List<Receiver> instruments = new ArrayList<>();
    instruments.add(new Sampler(ac, program));
    sequencer = new Sequencer(ac, instruments, midiSource);
    this.ac = ac;
    ac.out.addDependent(sequencer);
  }

  public void play() {
    info("Calling play...");
    if (!ac.isRunning()) {
      ac.start();
    }
    sequencer.start();
    info("Done calling play.");
  }

  @Override
  public void pause(boolean shouldPause) {
    if (shouldPause) {
      paused = true;
      sequencer.pause(shouldPause);
    } else {
      paused = false;
      play();
    }
  }


  @Override
  public void stop() {
    paused = true;
    sequencer.pause(true);
  }

  @Override
  public String getSource() {
    return midiSource.toString();
  }

  @Override
  public boolean isPaused() {
    return paused;
  }

  protected void info(String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
  }
}
