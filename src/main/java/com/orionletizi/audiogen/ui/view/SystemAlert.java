package com.orionletizi.audiogen.ui.view;

import javafx.scene.control.Alert;

public class SystemAlert implements IAlert {
  private final Alert alert;

  public SystemAlert(final Alert.AlertType type) {
    alert = new Alert(type);
  }

  @Override
  public void setTitle(final String text) {
    alert.setTitle(text);
  }

  @Override
  public void setHeaderText(final String text) {
    alert.setHeaderText(text);
  }

  @Override
  public void setContentText(final String text) {
    alert.setContentText(text);
  }

  @Override
  public void showAndWait() {
    alert.showAndWait();
  }
}
