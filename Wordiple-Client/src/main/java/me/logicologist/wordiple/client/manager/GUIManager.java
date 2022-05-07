package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.*;
import me.logicologist.wordiple.client.gui.controllers.auth.LoginController;
import me.logicologist.wordiple.client.gui.controllers.overlays.OverlayController;
import me.logicologist.wordiple.client.gui.controllers.overlays.ProfileOverlayController;
import me.logicologist.wordiple.client.gui.controllers.overlays.RankOverlayController;
import me.logicologist.wordiple.client.gui.controllers.auth.SignupConfirmController;
import me.logicologist.wordiple.client.gui.controllers.auth.SignupController;
import me.logicologist.wordiple.client.gui.controllers.select.GameSelectController;
import me.logicologist.wordiple.client.gui.controllers.select.PlayerHeaderController;
import me.logicologist.wordiple.client.gui.controllers.transitions.SwipeTransitionController;
import me.logicologist.wordiple.client.packets.StatInfoPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GUIManager extends Application {

    private static GUIManager instance;
    private static final List<Consumer<GUIManager>> readyListeners = new ArrayList<>();
    public Stage stage;

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;

        stage.setTitle("Wordiple");
        stage.setResizable(false);

        stage.setHeight(849);
        stage.setWidth(1456);

        showMainScreen(false);

        stage.show();

        stage.setOnCloseRequest(e -> {
            try {
                Platform.exit();
                PacketManager.getInstance().getSocket().shutdownClient();
                System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        readyListeners.forEach(x -> x.accept(this));
        readyListeners.clear();
    }

    public void showMainScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            if (fadeIn) ((MainScreenController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showLoginScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            if (fadeIn) ((LoginController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showSignupScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            if (fadeIn) ((SignupController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showSignupConfirmScreen(boolean fadeIn, String email, String username) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/signupconfirm.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            SignupConfirmController controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setUsername(username);
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showGameSelectScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameselect.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            attachPlayerHeader();
            PlayerHeaderController playerHeaderController = PlayerHeaderController.instance;
            playerHeaderController.setUsername(SessionManager.getInstance().getUsername());
            playerHeaderController.setLevel(SessionManager.getInstance().getLevel());
            playerHeaderController.setBarPercentage((double) SessionManager.getInstance().getCurrentXp() / SessionManager.getInstance().getNeededXp(), null);
            if (fadeIn) ((GameSelectController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LoadScreenController showLoadScreen(String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/loadscreen.fxml"));
            fxmlLoader.load();
            LoadScreenController loadScreenController = fxmlLoader.getController();
            loadScreenController.setText(title);
            loadScreenController.setParent((AnchorPane) stage.getScene().getRoot());
            loadScreenController.setParentPane();
            return loadScreenController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public SwipeTransitionController startSwipeTransition(Runnable runFirst, Runnable runAfter) {
        if (runFirst != null) runFirst.run();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/swipetransition.fxml"));
            fxmlLoader.load();
            SwipeTransitionController swipeTransitionController = fxmlLoader.getController();
            swipeTransitionController.setParent((AnchorPane) stage.getScene().getRoot());
            swipeTransitionController.transitionIn(() -> {
                runAfter.run();
                try {
                    FXMLLoader outTransition = new FXMLLoader(getClass().getResource("/swipetransition.fxml"));
                    outTransition.load();
                    SwipeTransitionController outTransitionController = outTransition.getController();
                    outTransitionController.setParent((AnchorPane) stage.getScene().getRoot());
                    outTransitionController.transitionOut();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            return swipeTransitionController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public RankOverlayController showRankOverlay(Runnable runAfter) {
        try {
            OverlayController overlayController = showOverlay(true);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/rankoverlay.fxml"));
            fxmlLoader.load();
            RankOverlayController rankOverlayController = fxmlLoader.getController();
            rankOverlayController.setParent((AnchorPane) stage.getScene().getRoot());
            rankOverlayController.attach();
            rankOverlayController.transitionIn(overlayController, runAfter);
            return rankOverlayController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void showProfileOverlay(String username, Runnable runAfter, Consumer<ProfileOverlayController> controller) {
        LoadScreenController loadScreenController = showLoadScreen("Fetching profile...");
        PacketManager.getInstance().getSocket().getPacket(StatInfoPacket.class).sendPacket(packet ->
                packet.getPacketType().getArguments().setValues("username", username)
        ).waitForResponse(packet -> {
            loadScreenController.remove(() -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/profileoverlay.fxml"));
                    fxmlLoader.load();
                    ProfileOverlayController profileOverlayController = fxmlLoader.getController();
                    OverlayController overlayController = showOverlay(true);
                    profileOverlayController.setParent((AnchorPane) stage.getScene().getRoot());
                    profileOverlayController.attach();
                    profileOverlayController.transitionIn(overlayController, runAfter);
                    if (controller != null) controller.accept(profileOverlayController);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            return false;
        }, () -> {
            loadScreenController.remove(() -> {
                LoadScreenController errorPopup = showLoadScreen("Error fetching data!");
                WordipleClient.getExecutor().schedule(() -> errorPopup.remove(null), 2, TimeUnit.SECONDS);
                runAfter.run();
            });
        }, 3, TimeUnit.SECONDS);
    }

    public OverlayController showOverlay(boolean visible) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/overlay.fxml"));
            fxmlLoader.load();
            OverlayController overlayController = fxmlLoader.getController();
            overlayController.setParent((AnchorPane) stage.getScene().getRoot());
            overlayController.attach();
            if (visible) {
                overlayController.transitionIn();
                return overlayController;
            }
            overlayController.addAsInvisible();
            return overlayController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void attachPlayerHeader() {
        try {
            if (PlayerHeaderController.instance == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/playerheader.fxml"));
                fxmlLoader.load();
            }
            PlayerHeaderController.instance.setParent((AnchorPane) stage.getScene().getRoot());
            PlayerHeaderController.instance.attach();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void launch(String[] args) {
        Application.launch(args);
    }

    public static void addReadyListener(Consumer<GUIManager> runnable) {
        if (instance != null) {
            Platform.runLater(() -> runnable.accept(instance));
            return;
        }
        readyListeners.add(runnable);
    }

    public static GUIManager getInstance() {
        return instance;
    }
}
