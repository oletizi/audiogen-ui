package com.orionletizi.audiogen.ui.proxy;

import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class AccordionProxy {
  private Accordion accordion;

  public AccordionProxy(final Accordion accordion) {
    this.accordion = accordion;
  }

  public void addTitledPane(String title, NodeProxy content) {
    final ObservableList<TitledPane> panes = this.accordion.getPanes();
    panes.add(new TitledPane(title, content.getNode()));
  }
}
