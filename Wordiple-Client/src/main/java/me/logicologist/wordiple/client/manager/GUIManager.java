package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.*;

import java.util.ArrayList;
import java.util.List;
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
//            if (fadeIn) ((GameSelectController) fxmlLoader.getController()).transitionIn();
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
