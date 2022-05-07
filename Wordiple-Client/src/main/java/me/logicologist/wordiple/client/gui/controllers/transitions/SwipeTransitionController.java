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

public class SwipeTransitionController extends AttachableAdapter {

    @FXML
    private AnchorPane movablePane;

    public void transitionIn(Runnable runnable) {
        super.attach();
        movablePane.setLayoutX(1640);

        Timeline timelineIn = new Timeline();
        timelineIn.setCycleCount(1);
        KeyValue kvIn = new KeyValue(movablePane.layoutXProperty(), 0);
        KeyFrame kfIn = new KeyFrame(Duration.millis(300), kvIn);
        timelineIn.getKeyFrames().add(kfIn);
        timelineIn.setAutoReverse(false);

        timelineIn.play();

        timelineIn.setOnFinished(x -> {
            super.detach();
            runnable.run();
        });
    }

    public void transitionOut() {
        super.attach();
        Timeline timelineOut = new Timeline();
        timelineOut.setCycleCount(1);
        KeyValue kvIn = new KeyValue(movablePane.layoutXProperty(), -1840);
        KeyFrame kfIn = new KeyFrame(Duration.millis(300), kvIn);
        timelineOut.getKeyFrames().add(kfIn);
        timelineOut.setAutoReverse(false);

        timelineOut.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
    }
}
