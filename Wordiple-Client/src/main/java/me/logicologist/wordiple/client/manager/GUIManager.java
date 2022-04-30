package me.logicologist.wordiple.client.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUIManager extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Wordiple");
        Font.loadFont(getClass().getResourceAsStream("NexaLight.ttf"), 16);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));

        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}
