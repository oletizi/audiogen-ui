package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.samplersong.domain.Instrument;
import com.orionletizi.audiogen.samplersong.domain.InstrumentVariants;
import com.orionletizi.audiogen.samplersong.domain.MidiPattern;

public class InstrumentVariantsDisplay<I extends Instrument, P extends MidiPattern> {
  private final InstrumentVariants<I, P> variants;

  public InstrumentVariantsDisplay(final InstrumentVariants<I, P> variants) {
    this.variants = variants;
  }

  public InstrumentVariants<I, P> getVariants() {
    return variants;
  }

  @Override
  public String toString() {
    return variants.getName();
  }
}
