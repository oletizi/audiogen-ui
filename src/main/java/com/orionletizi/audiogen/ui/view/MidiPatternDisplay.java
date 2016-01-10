package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.MidiPattern;

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
    return pattern.getMidiSource().toString();
  }
}
