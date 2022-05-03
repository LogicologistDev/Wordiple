package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.logicologist.wordiple.client.gui.controllers.LoginController;
import me.logicologist.wordiple.client.gui.controllers.MainScreenController;
import me.logicologist.wordiple.client.gui.controllers.SignupController;

public class GUIManager extends Application {

    private static GUIManager instance;

    public Stage stage;

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;

        stage.setTitle("Wordiple");
        stage.setResizable(false);

        stage.setHeight(810);
        stage.setWidth(1440);

        showMainScreen(false);

        stage.show();
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

    public static void launch(String[] args) {
        Application.launch(args);
    }

    public static GUIManager getInstance() {
        return instance;
    }
}
