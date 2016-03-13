package com.orionletizi.audiogen.ui.view;

public interface IAlert {
  void setTitle(String text);
  void setHeaderText(String text);
  void setContentText(String text);
  void showAndWait();
}
