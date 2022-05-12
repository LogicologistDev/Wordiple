package me.logicologist.wordiple.client.gui.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class LetterFieldPopAnimation {

    private final Label writableValue;
    private final double duration;

    public LetterFieldPopAnimation(Label writableValue, double duration) {
        this.writableValue = writableValue;
        this.duration = duration;
    }

    public void play() {
        Timeline popTimeline = new Timeline();
        for (int i = 10; i >= 1; i--) {
            if (i == 1) {
                popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue.scaleXProperty(), 1)));
                popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue.scaleYProperty(), 1)));
            }
            double v = Math.pow(2, -7 * (1 - 0.1 * i)) * 0.2 + 1;
            popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration - duration / 10 * i), new KeyValue(writableValue.scaleXProperty(), v)));
            popTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration - duration / 10 * i), new KeyValue(writableValue.scaleYProperty(), v)));
        }
        popTimeline.play();
    }

}
