package com.orionletizi.audiogen.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractController {


  @FXML
  private MenuBar menuBar;

  @FXML
  private SongPaneController songPaneController;

  @FXML
  private ChordalInstrumentPaneController chordalInstrumentPaneController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    songPaneController.setMainController(this);
    //chordalInstrumentPaneController.setMainController(this);
    final Menu menuFile = new Menu("File");
//    final MenuItem saveItem = new MenuItem("Save");
//    saveItem.setOnAction(event -> {
//      save();
//    });
//    saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
//    menuFile.getItems().add(saveItem);

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

  @SuppressWarnings("unused")
  public void setStage(Stage stage) {

  }

  public SongPaneController getSongPaneController() {
    return songPaneController;
  }

}
