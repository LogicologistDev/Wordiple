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
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.packets.auth.ForgotUrPasswordPacket;
import me.logicologist.wordiple.client.sound.SoundType;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This class is used as the controller for the forgot password screen.
 * It is used to handle the user input and send the appropriate packet(s) to the server.
 * This class is part of the authentication controller set.
 *
 * @author      Logicologist
 * @since       1.0
 */
public class ForgotPasswordController extends FadeVerticalTransitionAdapter {

    /**
     * The FXML fields for the forgot password screen.
     */
    @FXML
    private AnchorPane movablePane;

    @FXML
    private TextField emailField;

    @FXML
    private Button resetButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button mainScreenButton;

    private boolean midAction = false;

    /**
     * The method run on initialization.
     * This method is overridden from the Initializable interface.
     *
     * @see javafx.fxml.Initializable
     * @see javafx.fxml.Initializable#initialize(URL, ResourceBundle)
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
                GUIManager.getInstance().showLoginScreen(true);
            });
        });
        Runnable runnable = () -> {
            Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            if (emailField.getText().isEmpty() || !emailPattern.matcher(emailField.getText()).matches()) {
                errorMessageLabel.setText("Please enter a valid email address.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
            if (midAction) return;
            midAction = true;
            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Sending instructions...");
            PacketManager.getInstance().getSocket().getPacket(ForgotUrPasswordPacket.class).sendPacket(packet ->
                    packet.getPacketType().getArguments().setValues("email", emailField.getText())
            ).waitForResponse(args -> {
                String code = args.get("response", String.class);
                if (code == null) {
                    Platform.runLater(() -> {
                        errorMessageLabel.setText("That email hasn't been registered, check your email again.");
                        loadScreen.remove(null);
                        new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                    });
                    midAction = false;
                    return false;
                }
                loadScreen.remove(() -> Platform.runLater(() -> super.transitionOut(() -> GUIManager.getInstance().showResetPasswordScreen(true, emailField.getText()))));
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. The connection could not be established, or the server may be down.");
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
                    super.transitionOut(() -> GUIManager.getInstance().showLoginScreen(true));
                    return;
                case ENTER:
                    SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
                    runnable.run();
            }
        });
        resetButton.setOnAction(event -> runnable.run());
    }
}
