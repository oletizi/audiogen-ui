package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.ChordalMidiPattern;
import org.apache.commons.io.FilenameUtils;

public class ChordalMidiPatternDisplay {
  private ChordalMidiPattern pattern;

  public ChordalMidiPatternDisplay(final ChordalMidiPattern pattern) {

    this.pattern = pattern;
  }

  public ChordalMidiPattern getPattern() {
    return pattern;
  }

  public String toString() {
    return FilenameUtils.getName(pattern.getMidiSource().getPath());
  }
}
