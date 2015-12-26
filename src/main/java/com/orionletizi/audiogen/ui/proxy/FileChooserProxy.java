package com.orionletizi.audiogen.ui.proxy;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChooserProxy {

  private Window window;

  public FileChooserProxy(Window window) {
    this.window = window;
  }

  public File getFile() {
    return new FileChooser().showOpenDialog(window);
  }
}
