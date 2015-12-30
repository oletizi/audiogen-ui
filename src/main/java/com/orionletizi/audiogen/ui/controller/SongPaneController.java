package com.orionletizi.audiogen.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.audiogen.samplersong.domain.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SongPaneController extends AbstractController {

  @FXML
  private Button openSongButton;
  @FXML
  private Button newSongButton;
  @FXML
  private TextField songPathDisplay;


  @Override

  public void initialize(URL location, ResourceBundle resources) {
    openSongButton.setOnAction(event -> {
      final File file = new FileChooser().showOpenDialog(null);
      if (file != null) {
        openSong(file);
      }
    });
  }

  private void openSong(final File file) {
    final ObjectMapper mapper = mainController.getMapper();
    try {
      final Song song = mapper.readValue(file, Song.class);

    } catch (IOException e) {
      error("Error", "Error Loading Song File", "Error loading song file: " + file);
    }
  }

  private void error(final String title, final String header, final String message) {
    final Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(message);
    alert.showAndWait();
  }

}
