package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.manager.GUIManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends FadeTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button mainScreenButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);

        mainScreenButton.setOnAction(event -> {
            if (!mainScreenButton.isHover()) return;

            super.transitionOut(() -> {
                GUIManager.getInstance().showMainScreen(true);
            });
        });
    }
}
