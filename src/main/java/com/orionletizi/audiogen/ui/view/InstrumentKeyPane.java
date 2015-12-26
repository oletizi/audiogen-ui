package com.orionletizi.audiogen.ui.view;

import com.orionletizi.audiogen.ui.proxy.NodeProxy;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class InstrumentKeyPane implements NodeProxy {

  private Node node;

  public Node getNode() {
    return new Label("Hi");
  }
}
