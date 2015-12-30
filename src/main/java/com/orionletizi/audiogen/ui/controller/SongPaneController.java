package com.orionletizi.audiogen.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SongPaneController implements Initializable {

  @FXML
  private Button openSongButton;
  @FXML
  private Button newSongButton;
  @FXML
  private TextField songPathDisplay;

  @Override

  public void initialize(URL location, ResourceBundle resources) {
    System.out.println("========>HERE");
  }
}
