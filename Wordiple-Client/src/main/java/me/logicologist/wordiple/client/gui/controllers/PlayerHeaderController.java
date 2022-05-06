package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerHeaderController extends AttachableAdapter {

    @FXML
    private AnchorPane headerPane;

    public static PlayerHeaderController instance = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        super.setAttachment(headerPane);
    }
}
