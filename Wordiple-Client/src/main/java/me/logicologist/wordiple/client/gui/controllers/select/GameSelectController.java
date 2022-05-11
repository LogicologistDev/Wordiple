package me.logicologist.wordiple.client.gui.controllers.select;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeHorizontalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.packets.info.QueueInfoPacket;
import me.logicologist.wordiple.client.packets.info.StatInfoPacket;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

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

    @FXML
    private Button casualButton;

    @FXML
    private Button timeRoyaleButton;


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
            SoundManager.getInstance().stopSound(SoundType.BACKGROUND_MUSIC);

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
            if (true) {
                GUIManager.getInstance().showCompetitiveIntro(null, null, "uu360", 0, true);
                return;
            }
            if (midAction) return;
            midAction = true;

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Fetching Queue Data...");
            PacketManager.getInstance().getSocket().getPacket(StatInfoPacket.class).sendPacket(packet ->
                    packet.getPacketType().getArguments().setValues("username", SessionManager.getInstance().getUsername())
            ).waitForResponse(data -> {
                PacketManager.getInstance().getSocket().getPacket(QueueInfoPacket.class).sendPacket(packet ->
                        packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID()).setValues("queuetype", QueueType.COMPETITIVE)
                ).waitForResponse(queue -> {
                    loadScreenController.remove(() -> super.transitionOut(() -> GUIManager.getInstance().showCompetitiveQueueScreen(true, data, queue)));
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

        casualButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Fetching Queue Data...");
            PacketManager.getInstance().getSocket().getPacket(QueueInfoPacket.class).sendPacket(packet ->
                    packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID()).setValues("queuetype", QueueType.CASUAL)
            ).waitForResponse(queue -> {
                loadScreenController.remove(() -> super.transitionOut(() -> GUIManager.getInstance().showCasualQueueScreen(true, queue)));
                return false;
            }, () -> {
                loadScreenController.remove(() -> {
                    LoadScreenController errorPopup = GUIManager.getInstance().showLoadScreen("Error fetching data!");
                    WordipleClient.getExecutor().schedule(() -> errorPopup.remove(null), 2, TimeUnit.SECONDS);
                    midAction = false;
                });
            }, 10, TimeUnit.SECONDS);
        });

        timeRoyaleButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Fetching Queue Data...");
            PacketManager.getInstance().getSocket().getPacket(QueueInfoPacket.class).sendPacket(packet ->
                    packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID()).setValues("queuetype", QueueType.TIME_ROYALE)
            ).waitForResponse(queue -> {
                loadScreenController.remove(() -> super.transitionOut(() -> GUIManager.getInstance().showTimeRoyaleQueueScreen(true, queue)));
                return false;
            }, () -> {
                loadScreenController.remove(() -> {
                    LoadScreenController errorPopup = GUIManager.getInstance().showLoadScreen("Error fetching data!");
                    WordipleClient.getExecutor().schedule(() -> errorPopup.remove(null), 2, TimeUnit.SECONDS);
                    midAction = false;
                });
            }, 10, TimeUnit.SECONDS);
        });

        movablePane.setOnKeyReleased(event -> {
            if (midAction) return;
            midAction = true;
            if (event.getCode() != KeyCode.ESCAPE || GUIManager.getInstance() == null) return;

            SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
            GUIManager.getInstance().showConfirmExitOverlay(() -> midAction = false);
        });
    }

    public void setLevelBar() {
        double percent = SessionManager.getInstance().getLevel();
    }
}
