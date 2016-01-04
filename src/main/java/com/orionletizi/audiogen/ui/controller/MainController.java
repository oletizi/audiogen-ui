package com.orionletizi.audiogen.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractController {


  @FXML
  private MenuBar menuBar;

  @FXML
  private SongPaneController songPaneController;

  @FXML
  PlayerController audioPlayerController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    songPaneController.setMainController(this);
    final Menu menuFile = new Menu("File");

    final MenuItem quitItem = new MenuItem("Quit");
    quitItem.setOnAction(event -> quit());
    quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));

    menuFile.getItems().add(quitItem);
    menuBar.getMenus().clear();
    menuBar.getMenus().add(menuFile);
  }

  protected void quit() {
    getAudioContext().stop();
    System.exit(0);
  }
}
