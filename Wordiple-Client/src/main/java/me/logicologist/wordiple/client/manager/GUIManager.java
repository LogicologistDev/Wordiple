package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIManager extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Wordiple");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    public static void launch(String[] args) {
        Application.launch(args);
    }
}
