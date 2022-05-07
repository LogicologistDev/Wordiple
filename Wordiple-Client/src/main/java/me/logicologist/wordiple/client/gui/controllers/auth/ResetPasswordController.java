package me.logicologist.wordiple.client.gui.controllers.auth;

import com.google.common.hash.Hashing;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeVerticalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.packets.auth.ResetUrPasswordPacket;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ResetPasswordController extends FadeVerticalTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private TextField codeField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private Button mainScreenButton;

    private boolean midAction = false;

    private String email = null;
    private String code = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);
        mainScreenButton.setOnAction(event -> {
            if (midAction) return;
            if (!mainScreenButton.isHover()) return;
            midAction = true;

            super.transitionOut(() -> GUIManager.getInstance().showMainScreen(true));
        });

        resetButton.setOnAction(event -> {
            if (passwordField.getText().isEmpty() || passwordField.getText().length() <= 5) {
                errorMessageLabel.setText("Invalid password.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (confirmPasswordField.getText().isEmpty() || !passwordField.getText().equals(confirmPasswordField.getText())) {
                errorMessageLabel.setText("Passwords do not match.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (midAction) return;
            midAction = true;
            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Resetting...");

            StringBuilder salt = new StringBuilder();
            String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            for (int i = 0; i < 16; i++) {
                salt.append(saltChars.charAt(new Random().nextInt(saltChars.length())));
            }
            String passwordHash = Hashing.sha256().hashString(passwordField.getText() + salt, Charset.defaultCharset()).toString();

            PacketManager.getInstance().getSocket().getPacket(ResetUrPasswordPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                    .setValues("email", email)
                    .setValues("code", codeField.getText())
                    .setValues("password_hash", passwordHash)
                    .setValues("salt", salt.toString())
            ).waitForResponse(x -> {
                Platform.runLater(() -> {
                    loadScreen.remove(null);
                    if (!x.get("success", Boolean.class)) {
                        errorMessageLabel.setText("Incorrect code, please make sure you copied the code correctly.");
                        new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                        midAction = false;
                        return;
                    }
                    super.transitionOut(() -> GUIManager.getInstance().showLoginScreen(true));
                });
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. The connection could not be established, or the server may be down.");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                midAction = false;
            }), 10, TimeUnit.SECONDS);
        });
    }

    public void setEmail(String email) {
        if (this.email != null) return;

        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
