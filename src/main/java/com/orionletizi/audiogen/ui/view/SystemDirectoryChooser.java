package com.orionletizi.audiogen.ui.view;

import javafx.stage.DirectoryChooser;

import java.io.File;

public class SystemDirectoryChooser implements DChooser {

  private final DirectoryChooser chooser;

  public SystemDirectoryChooser() {
    chooser = new DirectoryChooser();
  }

  @Override
  public void setInitialDirectory(final File directory) {
    chooser.setInitialDirectory(directory);
  }

  @Override
  public void setTitle(final String title) {
    chooser.setTitle(title);
  }

  @Override
  public File showDialog() {
    return chooser.showDialog(null);
  }
}
