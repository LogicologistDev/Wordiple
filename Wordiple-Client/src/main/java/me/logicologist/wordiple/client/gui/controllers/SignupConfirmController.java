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
import me.logicologist.wordiple.client.packets.SignupConfirmPacket;
import me.logicologist.wordiple.client.packets.SignupPacket;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SignupConfirmController extends FadeTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private TextField codeField;

    @FXML
    private Button signupButton;

    @FXML
    private Button mainScreenButton;

    private boolean midAction = false;
    private String email = null;

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

        codeField.setOnKeyTyped(event -> {
            String string = codeField.getText();

            if (string.length() > 6) {
                codeField.setText(string.substring(0, 6));
                codeField.positionCaret(string.length());
            }
        });

        signupButton.setOnAction(event -> {
            Pattern pattern = Pattern.compile("[0-9]{6}");
            if (codeField.getText().length() != 6 || !pattern.matcher(codeField.getText()).matches()) {
                errorMessageLabel.setText("Invalid code. A code requires 6 digits.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (this.email == null) {
                errorMessageLabel.setText("An error occurred. You will need to restart the signup process.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }

            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Verifying code...", (AnchorPane) GUIManager.getInstance().stage.getScene().getRoot());

            PacketManager.getInstance().getSocket().getPacket(SignupConfirmPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                    .setValues("code", codeField.getText())
                    .setValues("email", this.email)
            ).waitForResponse(args -> {
                UUID response = args.get("response", UUID.class);
                if (response == null) {
                    Platform.runLater(() -> {
                        errorMessageLabel.setText("The code you entered is invalid. Please try again.");
                        loadScreen.remove(null);
                        new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                    });
                    return false;
                }
                if (midAction) return false;
                midAction = true;

                Platform.runLater(() -> {
                    loadScreen.remove(() -> {
                        super.transitionOut(() -> {
                            GUIManager.getInstance().showMainScreen(true);
                        });
                    });
                });
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. Please try again.");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
            }), 10, TimeUnit.SECONDS);
        });
    }

    public void setEmail(String email) {
        if (this.email != null) return;
        this.email = email;
    }
}
