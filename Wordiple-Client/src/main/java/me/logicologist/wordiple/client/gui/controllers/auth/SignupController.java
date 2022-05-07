package me.logicologist.wordiple.client.gui.controllers.auth;

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
import me.logicologist.wordiple.client.packets.auth.SignupPacket;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SignupController extends FadeVerticalTransitionAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField verifyPasswordField;

    @FXML
    private Button signupButton;

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

        signupButton.setOnAction(event -> {
            Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
            if (emailField.getText().isEmpty() || !emailPattern.matcher(emailField.getText()).matches()) {
                errorMessageLabel.setText("Invalid email.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (usernameField.getText().isEmpty() || !usernamePattern.matcher(usernameField.getText()).matches()) {
                errorMessageLabel.setText("Invalid username.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (passwordField.getText().isEmpty() || passwordField.getText().length() <= 5) {
                errorMessageLabel.setText("Invalid password.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (verifyPasswordField.getText().isEmpty() || !passwordField.getText().equals(verifyPasswordField.getText())) {
                errorMessageLabel.setText("Passwords do not match.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }

            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Sending verification...");
            PacketManager.getInstance().getSocket().getPacket(SignupPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                    .setValues("email", emailField.getText().toLowerCase())
                    .setValues("username", usernameField.getText())
                    .setValues("password", passwordField.getText())
            ).waitForResponse(args -> {
                String response = args.get("response", String.class);
                if (!response.equals("Success")) {
                    Platform.runLater(() -> {
                        errorMessageLabel.setText(response);
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
                            GUIManager.getInstance().showSignupConfirmScreen(true, emailField.getText(), usernameField.getText());
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
}
