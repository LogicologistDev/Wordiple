package me.logicologist.wordiple.client.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.packets.LoginPacket;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
                errorMessageLabel.setText("Usernames can only contain letters, numbers, underscores, and must be 3-16 characters.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (passwordField.getText().isEmpty()) {
                errorMessageLabel.setText("Password cannot be empty.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }

            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Logging in...", (AnchorPane) GUIManager.getInstance().stage.getScene().getRoot());
            PacketManager.getInstance().getSocket().getPacket(LoginPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("username", usernameField.getText())
                            .setValues("password", passwordField.getText())
            ).waitForResponse(args -> {
                UUID response = args.get("response", UUID.class);
                if (response == null) {
                    errorMessageLabel.setText("Invalid username or password. Please try again.");
                    loadScreen.remove(null);
                    new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                }
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. Are you connected to the internet? Is the connection being blocked?");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
            }), 10, TimeUnit.SECONDS);
        });
    }
}
