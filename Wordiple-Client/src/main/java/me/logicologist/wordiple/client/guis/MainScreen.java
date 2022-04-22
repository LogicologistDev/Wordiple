package me.logicologist.wordiple.client.guis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Wordiple");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainscreen.fxml"));

        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}
