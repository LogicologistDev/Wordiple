package me.logicologist.wordiple.client.gui.controllers.transitions;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;

import java.net.URL;
import java.util.ResourceBundle;

public class FlashScreenController extends AttachableAdapter {


    @FXML
    private AnchorPane movablePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
    }

    public void transitionIn() {
        super.attach();
        movablePane.setLayoutX(1);

        double duration = 2;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(movablePane.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(duration), new KeyValue(movablePane.scaleYProperty(), 0))
        );

        timeline.play();

        timeline.setOnFinished(x -> {
            super.detach();
        });
    }

}
