package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.manager.GUIManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController extends FadeTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    private boolean midAction = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);

        loginButton.setOnAction(event -> {
            if (midAction) return;
            if (!loginButton.isHover()) return;
            midAction = true;

            super.transitionOut(() -> {
                GUIManager.getInstance().showLoginScreen(true);
            });
        });
    }
}
