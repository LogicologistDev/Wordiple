package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.framework.PacketArguments;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.MainScreenController;
import me.logicologist.wordiple.client.gui.controllers.auth.*;
import me.logicologist.wordiple.client.gui.controllers.game.CompetitiveIntroController;
import me.logicologist.wordiple.client.gui.controllers.game.GameController;
import me.logicologist.wordiple.client.gui.controllers.game.GameTextOverlayController;
import me.logicologist.wordiple.client.gui.controllers.game.VersusTwoController;
import me.logicologist.wordiple.client.gui.controllers.overlays.ConfirmExitOverlayController;
import me.logicologist.wordiple.client.gui.controllers.overlays.OverlayController;
import me.logicologist.wordiple.client.gui.controllers.overlays.ProfileOverlayController;
import me.logicologist.wordiple.client.gui.controllers.overlays.RankOverlayController;
import me.logicologist.wordiple.client.gui.controllers.queue.CasualQueueController;
import me.logicologist.wordiple.client.gui.controllers.queue.CompetitiveQueueController;
import me.logicologist.wordiple.client.gui.controllers.queue.QueueController;
import me.logicologist.wordiple.client.gui.controllers.queue.TimeRoyaleQueueController;
import me.logicologist.wordiple.client.gui.controllers.select.GameSelectController;
import me.logicologist.wordiple.client.gui.controllers.select.PlayerHeaderController;
import me.logicologist.wordiple.client.gui.controllers.transitions.SwipeTransitionController;
import me.logicologist.wordiple.client.integration.IntegrationStatus;
import me.logicologist.wordiple.client.packets.auth.LogoutPacket;
import me.logicologist.wordiple.client.packets.info.StatInfoPacket;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GUIManager extends Application {

    private static GUIManager instance;
    private static final List<Consumer<GUIManager>> readyListeners = new ArrayList<>();

    private QueueController queueController;
    private GameController gameController;
    public Stage stage;

    @Override
    public void start(Stage stage) {
        WordipleClient.getLogger().info("Starting GUI");
        this.stage = stage;
        stage.setTitle("Wordiple");

        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            stage.setHeight(849);
            stage.setWidth(1439);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMaximized(true);
//            stage.setFullScreen(true);
        }
        if (!OS.contains("WIN")) {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            stage.setHeight(dimension.getHeight());
            stage.setWidth(dimension.getWidth());
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setFullScreen(true);
        }
        stage.getIcons().add(new Image("/icon.png"));
        SoundManager soundManager = new SoundManager();
        LibraryManager libraryManager = new LibraryManager();
        showMainScreen(false);
        stage.show();

        GenericManager.downloadAssets(this);
        WordipleClient.getExecutor().submit(() -> {
            soundManager.load(); // download and load sounds.
            libraryManager.load(); // download and load integrations.
        });
        stage.setOnCloseRequest(e -> {
            try {
                PacketManager.getInstance().getSocket().getPacket(LogoutPacket.class).sendPacket(packet ->
                        packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID()).setValues("logout", false));
                PacketManager.getInstance().getSocket().shutdownClient();
                WordipleClient.getExecutor().shutdownNow();
                SoundManager.getInstance().stopSounds();
                Platform.exit();
                System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        WordipleClient.getLogger().info("GUI started");
    }

    static void innit(GUIManager manager) {
        instance = manager;
        readyListeners.forEach(x -> x.accept(manager));
        readyListeners.clear();
    }

    public void showMainScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));
            this.loadScene(fxmlLoader.load());
            if (fadeIn) ((MainScreenController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showLoginScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
            this.loadScene(fxmlLoader.load());
            if (fadeIn) ((LoginController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showSignupScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            this.loadScene(fxmlLoader.load());
            if (fadeIn) ((SignupController) fxmlLoader.getController()).transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showSignupConfirmScreen(boolean fadeIn, String email, String username) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/signupconfirm.fxml"));
            this.loadScene(fxmlLoader.load());
            SignupConfirmController controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setUsername(username);
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showResetPasswordScreen(boolean fadeIn, String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resetpassword.fxml"));
            this.loadScene(fxmlLoader.load());
            ResetPasswordController controller = fxmlLoader.getController();
            controller.setEmail(email);
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showForgotPasswordScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/forgotpassword.fxml"));
            this.loadScene(fxmlLoader.load());
            ForgotPasswordController controller = fxmlLoader.getController();
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showGameSelectScreen(boolean fadeIn) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameselect.fxml"));
            this.loadScene(fxmlLoader.load());
            attachPlayerHeader();
            if (fadeIn) {
                ((GameSelectController) fxmlLoader.getController()).transitionIn(() -> SoundManager.getInstance().playSound(SoundType.BACKGROUND_MUSIC));
                return;
            }
            SoundManager.getInstance().playSound(SoundType.BACKGROUND_MUSIC);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCompetitiveQueueScreen(boolean fadeIn, PacketArguments playerInfo, PacketArguments queueInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/competitivequeue.fxml"));
            this.loadScene(fxmlLoader.load());
            attachPlayerHeader();
            CompetitiveQueueController controller = fxmlLoader.getController();
            controller.setActive(queueInfo.get("active", Integer.class));
            controller.setInfo(playerInfo);
            controller.setQueueButtonStyles("button-competitive-queue-enter-button", "button-competitive-queue-leave-button");
            this.queueController = controller;
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCasualQueueScreen(boolean fadeIn, PacketArguments queueInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/casualqueue.fxml"));
            this.loadScene(fxmlLoader.load());
            attachPlayerHeader();
            CasualQueueController controller = fxmlLoader.getController();
            controller.setActive(queueInfo.get("active", Integer.class));
            controller.setQueueButtonStyles("button-casual-queue-enter-button", "button-casual-queue-leave-button");
            this.queueController = controller;
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showTimeRoyaleQueueScreen(boolean fadeIn, PacketArguments queueInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/timeroyalequeue.fxml"));
            this.loadScene(fxmlLoader.load());
            attachPlayerHeader();
            TimeRoyaleQueueController controller = fxmlLoader.getController();
            controller.setActive(queueInfo.get("active", Integer.class));
            controller.setQueueButtonStyles("button-time-royale-queue-enter-button", "button-time-royale-queue-leave-button");
            this.queueController = controller;
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showVersusTwoBoard(PacketArguments gameInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/versusboards/versustwo.fxml"));
            this.loadScene(fxmlLoader.load());
            VersusTwoController controller = fxmlLoader.getController();
            controller.setGameMeta(gameInfo.get("goal", String.class), gameInfo.get("opponent", String.class), gameInfo.get("queue", QueueType.class));
            this.gameController = controller;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public QueueController getQueueController() {
        return queueController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void resetQueueController() {
        this.queueController = null;
    }

    public void resetGameController() {
        this.gameController = null;
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

    public CompetitiveIntroController showCompetitiveIntro(Runnable runFirst, Runnable runAfterIn, Runnable runAfterOut, String opponentName, int opponentRating) {
        if (runFirst != null) runFirst.run();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/versusintrocompetitive.fxml"));
            handleBigAttachment(fxmlLoader.load());
            CompetitiveIntroController competitiveIntroController = fxmlLoader.getController();
            competitiveIntroController.setParent((AnchorPane) stage.getScene().getRoot());
            competitiveIntroController.setOpponentData(opponentName, opponentRating);
            SoundManager.getInstance().stopSound(SoundType.BACKGROUND_MUSIC);
            competitiveIntroController.transitionIn(() -> {
                SoundManager.getInstance().playSound(SoundType.FIGHTING_MUSIC);
                WordipleClient.getExecutor().schedule(() -> {
                    Platform.runLater(() -> {
                        try {
                            runAfterIn.run();
                            FXMLLoader outTransition = new FXMLLoader(getClass().getResource("/versusintrocompetitive.fxml"));
                            handleBigAttachment(outTransition.load());
                            CompetitiveIntroController competitiveOutroController = outTransition.getController();
                            competitiveOutroController.setParent((AnchorPane) stage.getScene().getRoot());
                            competitiveOutroController.setOpponentData(opponentName, opponentRating);
                            competitiveOutroController.transitionOut(runAfterOut);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }, 5, TimeUnit.SECONDS);
            });
            return competitiveIntroController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public RankOverlayController showRankOverlay(Runnable runAfter) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/rankoverlay.fxml"));
            fxmlLoader.load();
            RankOverlayController rankOverlayController = fxmlLoader.getController();
            OverlayController overlayController = showOverlay(true);
            rankOverlayController.setParent((AnchorPane) stage.getScene().getRoot());
            rankOverlayController.attach();
            rankOverlayController.transitionIn(overlayController, runAfter);
            return rankOverlayController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ConfirmExitOverlayController showConfirmExitOverlay(Runnable runAfter) {
        try {
            OverlayController overlayController = showOverlay(true);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/confirmexitoverlay.fxml"));
            fxmlLoader.load();
            ConfirmExitOverlayController confirmExitOverlayController = fxmlLoader.getController();
            confirmExitOverlayController.setParent((AnchorPane) stage.getScene().getRoot());
            confirmExitOverlayController.attach();
            confirmExitOverlayController.transitionIn(overlayController, runAfter);
            return confirmExitOverlayController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void showProfileOverlay(String username, Runnable runAfter) {
        LoadScreenController loadScreenController = showLoadScreen("Fetching profile...");
        PacketManager.getInstance().getSocket().getPacket(StatInfoPacket.class).sendPacket(packet ->
                packet.getPacketType().getArguments().setValues("username", username)
        ).waitForResponse(packet -> {
            loadScreenController.remove(() -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/profileoverlay.fxml"));
                    fxmlLoader.load();
                    ProfileOverlayController profileOverlayController = fxmlLoader.getController();
                    profileOverlayController.setData(packet);
                    OverlayController overlayController = showOverlay(true);
                    profileOverlayController.setParent((AnchorPane) stage.getScene().getRoot());
                    profileOverlayController.attach();
                    profileOverlayController.transitionIn(overlayController, runAfter);
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
            return overlayController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void showGameTextOverlay(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gametextoverlay.fxml"));
            fxmlLoader.load();
            GameTextOverlayController overlayController = fxmlLoader.getController();
            overlayController.setParent((AnchorPane) stage.getScene().getRoot());
            overlayController.attach();
            overlayController.setTextLabel(text);
            overlayController.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public Scene loadScene(Parent parent) {
        if (stage.getScene() != null) {
            stage.getScene().setRoot(parent);
            handleScene(stage.getScene());
            return null;
        }
        Scene scene = new Scene(parent);
        handleScene(scene);
        stage.setScene(scene);
        return scene;
    }

    public void handleScene(Scene scene) {
        this.handleDimension(Toolkit.getDefaultToolkit().getScreenSize(), scene);
//        stage.widthProperty().addListener((arg0, arg1, arg2) -> {
//            Dimension dimension = new Dimension();
//            dimension.setSize(arg2.doubleValue(), scene.getHeight());
//            handleDimension(dimension, scene);
//        });
//        stage.heightProperty().addListener((arg0, arg1, arg2) -> {
//            Dimension dimension = new Dimension();
//            dimension.setSize(scene.getWidth(), arg2.doubleValue());
//            handleDimension(dimension, scene);
//        });
    }

    private void handleDimension(Dimension dimension, Scene scene) {
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        double w = width / (width < 1440 ? width : 1440);
        double h = height / (height < 810 ? height : 810);
        Scale scale = new Scale(w, h, 0, 0);
        scene.getRoot().getTransforms().setAll(scale);
    }

    // Used to resize ATTACHABLE 1920X1808 SCREENS (will be new standard for higher quality later)
    private void handleBigAttachment(Parent parent) {
        Scale scale = new Scale(0.75, 0.75, 0, 0);
        parent.getTransforms().setAll(scale);
    }

    public static void launch(String[] args) {
        WordipleClient.getLogger().info("Launching Wordiple GUI");
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
