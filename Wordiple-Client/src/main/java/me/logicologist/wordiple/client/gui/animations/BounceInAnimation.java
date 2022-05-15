package me.logicologist.wordiple.client.gui.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

public class BounceInAnimation {

    private final WritableValue writableValue;
    private final int finalValue;
    private final int originalValue;
    private final int change;
    private final int extra;
    private final int duration;

    public BounceInAnimation(WritableValue writableValue, int change, int duration) {
        this.writableValue = writableValue;
        this.originalValue = (int) writableValue.getValue();
        this.finalValue = originalValue + change;
        this.change = change;
        this.duration = duration;
        this.extra = change / 10;
    }

    public void play() {
        Timeline bounceInTimeline = new Timeline();
        for (int i = 10; i >= 1; i--) {
            if (i == 1) {
                bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue, finalValue)));
            }
            double v = Math.pow(2, -7 * (1 - 0.1 * i)) * finalValue + originalValue;
            bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1 - 0.1 / 10 * i), new KeyValue(writableValue, v)));
        }

        for (int i = 0; i <= 10; i++) {
            if (i == 10) {
                bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * 0.9), new KeyValue(writableValue, finalValue + extra)));
                continue;
            }
            double v = Math.pow(2, -7 * (0.1 * i)) * (change + extra) + (finalValue - extra);
            bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * 0.9 / 10 * i), new KeyValue(writableValue, v)));
        }


        for (int i = 1; i <= 10; i++) {
            if (i == 10) {
                bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), new KeyValue(writableValue, finalValue)));
                continue;
            }
            double v = Math.pow(2, -7 * (0.1 * i)) * extra + finalValue;
            bounceInTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration * 0.1 / 10 * i + duration * 0.9), new KeyValue(writableValue, v)));
        }

        bounceInTimeline.play();
    }
}
