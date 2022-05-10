package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeVerticalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.sound.SoundType;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController extends FadeVerticalTransitionAdapter {

    @FXML
    public AnchorPane movablePane;

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

        signUpButton.setOnAction(event -> {
            if (midAction) return;
            if (!signUpButton.isHover()) return;
            midAction = true;

            super.transitionOut(() -> {
                GUIManager.getInstance().showSignupScreen(true);
            });
        });

        movablePane.setOnKeyReleased(event -> {
            if (midAction) return;
            midAction = true;
            if (event.getCode() != KeyCode.ESCAPE || GUIManager.getInstance() == null) return;

            SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
            GUIManager.getInstance().showConfirmExitOverlay(() -> midAction = false);
        });
    }
}
