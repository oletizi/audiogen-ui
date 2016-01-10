package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.MidiPattern;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;

public class MidiPatternDisplay {
  private MidiPattern pattern;

  public MidiPatternDisplay(final MidiPattern pattern) {
    this.pattern = pattern;
  }

  public MidiPattern getPattern() {
    return pattern;
  }

  @Override
  public String toString() {
    final URL midiSource = pattern.getMidiSource();
    final String basename = FilenameUtils.getBaseName(midiSource.getFile());
    return basename + ": " + midiSource.toString();
  }
}
