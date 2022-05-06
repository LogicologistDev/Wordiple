package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class GameSelectController extends FadeHorizontalTransitionAdapter {

    public static GameSelectController instance;

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Button logoutButton;

    @FXML
    private Button rankButton;

    private boolean midAction = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);

        logoutButton.setOnAction(event -> {
            if (!logoutButton.isHover()) return;
            if (midAction) return;
            midAction = true;

            LoadScreenController controller = GUIManager.getInstance().showLoadScreen("Logging out...");
            SessionManager.getInstance().setLocalSessionID(null);

            controller.remove(() -> {
                GUIManager.getInstance().startSwipeTransition(null, () -> GUIManager.getInstance().showMainScreen(false));
            });
        });

        rankButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;
            if (!rankButton.isHover()) return;

            GUIManager.getInstance().showRankOverlay(() -> {
                midAction = false;
            });
        });

    }

    public void setLevelBar() {
        double percent = SessionManager.getInstance().getLevel();
    }
}
