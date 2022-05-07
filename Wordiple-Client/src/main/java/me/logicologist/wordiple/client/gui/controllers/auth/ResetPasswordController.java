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
import me.logicologist.wordiple.client.packets.auth.ResetUrPasswordPacket;

import java.net.URL;
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
            if (!codeField.getText().equals(code)) {
                errorMessageLabel.setText("Incorrect code, please make sure you copied the code correctly.");
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
                return;
            }
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
            LoadScreenController loadScreen = GUIManager.getInstance().showLoadScreen("Resetting...");
            PacketManager.getInstance().getSocket().getPacket(ResetUrPasswordPacket.class).sendPacket(packet ->
                    packet.getPacketType().getArguments().setValues("email", email).setValues("password", passwordField.getText())
            ).waitForResponse(x -> {
                loadScreen.remove(() -> Platform.runLater(() -> GUIManager.getInstance().showLoginScreen(true)));
                return false;
            }, () -> Platform.runLater(() -> {
                errorMessageLabel.setText("Timed out. The connection could not be established, or the server may be down.");
                loadScreen.remove(null);
                new ShakeAnimation(2, movablePane.layoutXProperty(), 200).play();
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