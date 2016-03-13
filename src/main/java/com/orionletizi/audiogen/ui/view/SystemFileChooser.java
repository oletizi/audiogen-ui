package com.orionletizi.audiogen.ui.view;

import javafx.stage.FileChooser;

import java.io.File;

public class SystemFileChooser implements FChooser {
  final FileChooser fileChooser = new FileChooser();

  @Override
  public void setTitle(final String title) {
    fileChooser.setTitle(title);
  }

  @Override
  public void setInitialDirectory(final File file) {
    fileChooser.setInitialDirectory(file);
  }

  @Override
  public File showOpenDialog() {
    return fileChooser.showOpenDialog(null);
  }

}
