package com.orionletizi.audiogen.ui.view;

import javafx.scene.control.Alert;

public interface AlertFactory {
  IAlert newAlert(Alert.AlertType  type);
}
