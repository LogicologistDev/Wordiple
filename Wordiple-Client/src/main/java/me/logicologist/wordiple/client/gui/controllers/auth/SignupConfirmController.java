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
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.packets.auth.SignupConfirmPacket;
import me.logicologist.wordiple.client.packets.info.UserInfoPacket;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This class is used as the controller for the signup confirmation screen.
 * It is used to handle the user input and send the appropriate packet(s) to the server.
 * This class is part of the authentication controller set.
 *
 * @author      Logicologist
 * @since       1.0
 */
public class SignupConfirmController extends FadeVerticalTransitionAdapter {

    /**
     * The FXML fields for the signup confirmation screen.
     */
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

    @FXML
    private Label titleLabel;

    private boolean midAction = false;
    private String email = null;

    /**
     * The method run on initialization.
     * This method is overridden from the Initializable interface.
     *
     * @see javafx.fxml.Initializable
     * @param url The location of the FXML file.
     * @param resourceBundle The resources used by the FXML file.
     */
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
        Runnable runnable = () -> {
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
            SessionManager manager = SessionManager.getInstance();
            if (manager.getVersion() != null && !Utils.getVersion().equals(manager.getVersion())) {
                errorMessageLabel.setText("You are using an outdated version of the client. Please update.");
                return;
            }
            if (midAction) return;
            midAction = true;
            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Verifying code...");
            PacketManager.getInstance().getSocket().getPacket(SignupConfirmPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                    .setValues("code", codeField.getText())
                    .setValues("email", this.email)
            ).waitForResponse(args -> {
                UUID uuidResponse = args.get("response", UUID.class);
                if (uuidResponse == null) {
                    Platform.runLater(() -> {
                        errorMessageLabel.setText("The code you entered is invalid. Please try again.");
                        loadScreen.remove(null);
                        new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                        midAction = false;
                    });
                    return false;
                }
                manager.setLocalSessionID(uuidResponse, true);
                Platform.runLater(() -> {
                    PacketManager.getInstance().getSocket().getPacket(UserInfoPacket.class)
                            .sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(uuidResponse))
                            .waitForResponse(response -> {
                                String username = response.get("username", String.class);
                                if (username == null) {
                                    GUIManager.addReadyListener(instance -> instance.showLoginScreen(true));
                                    return false;
                                }
                                manager.load(response, username);
                                if (!Utils.getVersion().equals(manager.getVersion())) {
                                    Platform.runLater(() -> {
                                        loadScreen.remove(null);
                                        errorMessageLabel.setText("You are using an outdated version of the client. Please update.");
                                    });
                                    PacketManager.getInstance().getSocket().shutdownClient();
                                    return false;
                                }
                                loadScreen.remove(() -> {
                                    super.transitionOut(() -> {
                                        GUIManager.getInstance().startSwipeTransition(null, () -> GUIManager.getInstance().showGameSelectScreen(false));
                                    });
                                });
                                return false;
                            }, () -> {
                                errorMessageLabel.setText("Unable to verify session ID. Please try again.");
                                loadScreen.remove(null);
                                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                                midAction = false;
                            }, 5, TimeUnit.SECONDS);
                });
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. Please try again.");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                midAction = false;
            }), 10, TimeUnit.SECONDS);
        };
        movablePane.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (midAction) return;
                    midAction = true;

                    SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
                    super.transitionOut(() -> GUIManager.getInstance().showMainScreen(true));
                    return;
                case ENTER:
                    SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
                    runnable.run();
            }
        });
        signupButton.setOnAction(event -> {
            runnable.run();
        });
    }

    /**
     * Sets the email of the user to be then used to create a new account.
     * Typically called from SignupController.
     *
     * @see SignupController
     * @param email The email of the user.
     */
    public void setEmail(String email) {
        if (this.email != null) return;
        this.email = email;
    }

    /**
     * Sets the email of the user to be then used to create a new account.
     * Typically called from SignupController.
     *
     * @see me.logicologist.wordiple.client.gui.controllers.auth.SignupController
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.titleLabel.setText("Hello, " + username + "!");
    }
}
