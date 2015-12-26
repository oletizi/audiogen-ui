package com.orionletizi.audiogen.ui.proxy;

import javafx.scene.control.TextField;

public class TextFieldProxy {
  private TextField textField;

  public TextFieldProxy(final TextField textField) {
    this.textField = textField;
  }


  public void setText(final String text) {
    textField.setText(text);
  }

  public String getText() {
    return textField.getText();
  }
}
