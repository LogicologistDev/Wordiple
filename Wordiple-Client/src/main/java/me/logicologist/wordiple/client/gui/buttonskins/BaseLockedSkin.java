package me.logicologist.wordiple.client.gui.buttonskins;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.sound.SoundType;

public class BaseLockedSkin extends ButtonSkin {

    private boolean pressed;

    private boolean exited;

    public BaseLockedSkin(Button button) {
        super(button);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(50));
        fadeIn.setNode(button);
        fadeIn.setToValue(0.8);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(50));
        fadeOut.setNode(button);
        fadeOut.setToValue(0.6);

        FadeTransition fadePress = new FadeTransition(Duration.millis(50));
        fadePress.setNode(button);
        fadePress.setToValue(1);

        DropShadow fromShadow = new DropShadow(0, Color.BLACK);
        DropShadow hoverShadow = new DropShadow(5, Color.BLACK);
        DropShadow pressedShadow = new DropShadow(3, Color.BLACK);


        button.setOnMouseEntered(e -> {
            fadeIn.playFromStart();
            button.setEffect(hoverShadow);
            exited = false;
        });

        button.setOnMouseExited(e -> {
            exited = true;
            if (pressed) return;
            button.setEffect(fromShadow);
            fadeOut.playFromStart();
        });

        button.setOnMousePressed(e -> {
            fadePress.playFromStart();
            button.setEffect(pressedShadow);
            pressed = true;
        });

        button.setOnMouseReleased(e -> {
            pressed = false;
            if (!exited) {
                fadeIn.play();
                button.setEffect(hoverShadow);
                if (e.getButton() == MouseButton.PRIMARY) SoundManager.getInstance().playSound(SoundType.BUTTON_CLICK);
                return;
            }
            fadeOut.play();
            button.setEffect(fromShadow);
        });

        button.setOpacity(0.6);

    }
}
