package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIManager extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Wordiple");
        stage.setMaxHeight(1080);
        stage.setMaxWidth(1920);
        stage.setHeight(1080);
        stage.setWidth(1920);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    public static void launch(String[] args) {
        Application.launch(args);
    }
}
