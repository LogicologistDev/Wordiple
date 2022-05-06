package me.logicologist.wordiple.client.gui.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadScreenController extends AttachableAdapter {

    @FXML
    public AnchorPane movablePane;

    @FXML
    public Label displayLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        movablePane.setOpacity(0);
    }

    public void setText(String text) {
        displayLabel.setText(text);
    }

    public void setParentPane() {
        super.setAttachment(movablePane);
        super.attach();
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500));
        fadeIn.setNode(movablePane);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public void remove(Runnable runAfter) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500));
        fadeOut.setNode(movablePane);
        fadeOut.setToValue(0);
        fadeOut.play();
        fadeOut.setOnFinished(event -> {
            super.detach();
            if (runAfter != null) runAfter.run();
        });
    }
}
