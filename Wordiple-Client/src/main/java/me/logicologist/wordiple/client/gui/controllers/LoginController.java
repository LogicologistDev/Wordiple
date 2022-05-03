package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.manager.GUIManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

    private boolean midAction = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);

        mainScreenButton.setOnAction(event -> {
            if (midAction) return;
            if (!mainScreenButton.isHover()) return;
            midAction = true;

            super.transitionOut(() -> {
                GUIManager.getInstance().showMainScreen(true);
            });
        });

        loginButton.setOnAction(event -> {
            Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
            errorMessageLabel.setText("");
            if (!usernamePattern.matcher(usernameField.getText()).matches()) {
                errorMessageLabel.setText("Invalid username");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
        });
    }
}
