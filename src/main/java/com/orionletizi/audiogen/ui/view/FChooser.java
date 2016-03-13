package com.orionletizi.audiogen.ui.view;

import java.io.File;

public interface FChooser {

  void setTitle(String title);
  void setInitialDirectory(File file);
  File showOpenDialog();
}
