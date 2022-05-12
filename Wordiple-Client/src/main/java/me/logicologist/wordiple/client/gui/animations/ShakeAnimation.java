package me.logicologist.wordiple.client.gui.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ShakeAnimation {

    private static List<WritableValue> inProgressAnimations = new ArrayList<>();
    private final double intensity;
    private final WritableValue writableValue;
    private final int duration;

    public ShakeAnimation(double intensity, WritableValue writableValue, int duration) {
        this.intensity = intensity;
        this.writableValue = writableValue;
        this.duration = duration;
    }

    public void play() {
        if (inProgressAnimations.contains(writableValue)) return;
        inProgressAnimations.add(writableValue);
        int portions = this.duration / 12;
        Timeline shakeOne = new Timeline();
        shakeOne.setCycleCount(1);
        KeyValue kvOne = new KeyValue(writableValue, 10 * intensity + (double) writableValue.getValue());
        KeyFrame kfOne = new KeyFrame(Duration.millis(portions * 2), kvOne);
        shakeOne.getKeyFrames().add(kfOne);
        shakeOne.setAutoReverse(false);
        shakeOne.play();
        shakeOne.setOnFinished(event -> {
            Timeline shakeTwo = new Timeline();
            shakeTwo.setCycleCount(1);
            KeyValue kvTwo = new KeyValue(writableValue, -20 * intensity + (double) writableValue.getValue());
            KeyFrame kfTwo = new KeyFrame(Duration.millis(portions * 3), kvTwo);
            shakeTwo.getKeyFrames().add(kfTwo);
            shakeTwo.setAutoReverse(false);
            shakeTwo.play();
            shakeTwo.setOnFinished(event2 -> {
                Timeline shakeThree = new Timeline();
                shakeThree.setCycleCount(1);
                KeyValue kvThree = new KeyValue(writableValue, 15 * intensity + (double) writableValue.getValue());
                KeyFrame kfThree = new KeyFrame(Duration.millis(portions * 4), kvThree);
                shakeThree.getKeyFrames().add(kfThree);
                shakeThree.setAutoReverse(false);
                shakeThree.play();
                shakeThree.setOnFinished(event3 -> {
                    Timeline shakeFour = new Timeline();
                    shakeFour.setCycleCount(1);
                    KeyValue kvFour = new KeyValue(writableValue, -5 * intensity + (double) writableValue.getValue());
                    KeyFrame kfFour = new KeyFrame(Duration.millis(portions * 5), kvFour);
                    shakeFour.getKeyFrames().add(kfFour);
                    shakeFour.setAutoReverse(false);
                    shakeFour.play();
                    shakeFour.setOnFinished(event4 -> {
                        inProgressAnimations.remove(writableValue);
                    });
                });
            });
        });
    }
}
