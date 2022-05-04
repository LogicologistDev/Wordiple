package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.logicologist.wordiple.client.gui.controllers.*;

public class GUIManager extends Application {

    private static GUIManager instance;

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

    public void showSignupConfirmScreen(boolean fadeIn, String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/signupconfirm.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            SignupConfirmController controller = fxmlLoader.getController();
            controller.setEmail(email);
            if (fadeIn) controller.transitionIn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LoadScreenController showLoadScreen(String title, Pane parentPane) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/loadscreen.fxml"));
            fxmlLoader.load();
            LoadScreenController loadScreenController = fxmlLoader.getController();
            loadScreenController.setText(title);
            loadScreenController.setParentPane(parentPane);
            return loadScreenController;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void launch(String[] args) {
        Application.launch(args);
    }

    public static GUIManager getInstance() {
        return instance;
    }
}
