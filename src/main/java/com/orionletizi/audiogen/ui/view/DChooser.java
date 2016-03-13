package com.orionletizi.audiogen.ui.view;

import java.io.File;

public interface DChooser {
  void setInitialDirectory(File directory);
  void setTitle(String title);
  File showDialog();
}
