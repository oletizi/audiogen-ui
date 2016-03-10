package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.domain.Instrument;

public class InstrumentDisplay<T extends Instrument> {
  private T instrument;

  public InstrumentDisplay(final T instrument) {
    this.instrument = instrument;
  }

  public T getInstrument() {
    return instrument;
  }

  public String toString() {
    return instrument.getName() + ": " + instrument.getPath();
  }

}
