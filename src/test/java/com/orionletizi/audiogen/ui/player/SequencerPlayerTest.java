package com.orionletizi.audiogen.ui.player;

import com.orionletizi.sampler.SamplerProgram;
import com.orionletizi.sampler.sfz.SfzParser;
import com.orionletizi.sampler.sfz.SfzSamplerProgram;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class SequencerPlayerTest {
  @Test
  public void testBasics() throws Exception {
    final URL midiResource = ClassLoader.getSystemResource("midi/GT1.mid");
    final URL instrumentResource = ClassLoader.getSystemResource("sfz/guitar3/guitar3.sfz");
    final SamplerProgram program = new SfzSamplerProgram(new SfzParser(), new File(instrumentResource.getPath()));
    final AudioIO io = new JavaSoundAudioIO();
    final SequencerPlayer player = new SequencerPlayer(new AudioContext(io), program, midiResource);

    player.play();
    synchronized (this) {
      wait(10 * 1000);
    }
  }
}