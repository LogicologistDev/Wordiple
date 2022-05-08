package me.logicologist.wordiple.client.gui.controllers.select;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeHorizontalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.packets.info.QueueInfoPacket;
import me.logicologist.wordiple.client.packets.info.UserInfoPacket;
import me.logicologist.wordiple.common.packets.AuthPacketType;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class GameSelectController extends FadeHorizontalTransitionAdapter {

    public static GameSelectController instance;

    @FXML
    private AnchorPane movablePane;

    @FXML
    private Button logoutButton;

    @FXML
    private Button rankButton;

    @FXML
    private Button competitiveButton;

    private boolean midAction = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setPane(movablePane);

        logoutButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            LoadScreenController controller = GUIManager.getInstance().showLoadScreen("Logging out...");
            SessionManager.getInstance().setLocalSessionID(null, true);
            SessionManager.getInstance().setLoggedIn(false);

            controller.remove(() -> {
                GUIManager.getInstance().startSwipeTransition(null, () -> GUIManager.getInstance().showMainScreen(false));
            });
        });

        rankButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            GUIManager.getInstance().showRankOverlay(() -> {
                midAction = false;
            });
        });

        competitiveButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Fetching Data...");
            PacketManager.getInstance().getSocket().getPacket(UserInfoPacket.class).sendPacket(packet ->
                    packet.getPacketType().getArguments().setValues("username", SessionManager.getInstance().getUsername())
            ).waitForResponse(data -> {
                PacketManager.getInstance().getSocket().getPacket(QueueInfoPacket.class).sendPacket(packet ->
                        packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID()).setValues("queuetype", "competitive")
                ).waitForResponse(queue -> {
                    super.transitionOut(() -> GUIManager.getInstance().showCompetitiveQueueScreen(true, data, queue));
                    return false;
                }, () -> {
                    loadScreenController.remove(() -> {
                        LoadScreenController errorPopup = GUIManager.getInstance().showLoadScreen("Error fetching data!");
                        WordipleClient.getExecutor().schedule(() -> errorPopup.remove(null), 2, TimeUnit.SECONDS);
                        midAction = false;
                    });
                }, 10, TimeUnit.SECONDS);
                return false;
            }, () -> {
                loadScreenController.remove(() -> {
                    LoadScreenController errorPopup = GUIManager.getInstance().showLoadScreen("Error fetching data!");
                    WordipleClient.getExecutor().schedule(() -> errorPopup.remove(null), 2, TimeUnit.SECONDS);
                    midAction = false;
                });
            }, 10, TimeUnit.SECONDS);
        });

    }

    public void setLevelBar() {
        double percent = SessionManager.getInstance().getLevel();
    }
}
