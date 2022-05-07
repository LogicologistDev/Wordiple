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
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.packets.UserInfoPacket;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.client.packets.auth.LoginPacket;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginController extends FadeVerticalTransitionAdapter {

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

    @FXML
    private Button forgotPasswordButton;

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

            if (midAction) return;
            midAction = true;
            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Logging in...");
            PacketManager.getInstance().getSocket().getPacket(LoginPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                    .setValues("username", usernameField.getText())
                    .setValues("password", passwordField.getText())
            ).waitForResponse(args -> {
                UUID uuidResponse = args.get("response", UUID.class);
                if (uuidResponse == null) {
                    Platform.runLater(() -> {
                        errorMessageLabel.setText("Invalid username or password. Please try again.");
                        loadScreen.remove(null);
                        new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                        midAction = false;
                    });
                    return false;
                }
                SessionManager.getInstance().setLocalSessionID(uuidResponse);
                Platform.runLater(() -> {
                    PacketManager.getInstance().getSocket().getPacket(UserInfoPacket.class)
                            .sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(uuidResponse))
                            .waitForResponse(response -> {
                                String username = response.get("username", String.class);
                                if (username == null) {
                                    GUIManager.addReadyListener(instance -> instance.showLoginScreen(true));
                                    return false;
                                }
                                SessionManager.getInstance().load(response, username);
                                loadScreen.remove(() -> {
                                    super.transitionOut(() -> {
                                        GUIManager.getInstance().startSwipeTransition(null, () -> GUIManager.getInstance().showGameSelectScreen(false));
                                    });
                                });
                                return false;
                            }, () -> {
                                midAction = false;
                                errorMessageLabel.setText("Unable to verify session ID. Please try again.");
                                loadScreen.remove(null);
                                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                            }, 5, TimeUnit.SECONDS);
                });
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. The connection could not be established, or the server may be down.");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                midAction = false;
            }), 10, TimeUnit.SECONDS);
        });
        forgotPasswordButton.setOnAction(event -> super.transitionOut(() -> GUIManager.getInstance().showForgotPasswordScreen(true)));
    }
}
