package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class CompetitiveIntroController extends AttachableAdapter {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private AnchorPane playerSide;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerRatingLabel;

    @FXML
    private AnchorPane opponentSide;

    @FXML
    private Label opponentNameLabel;

    @FXML
    private Label opponentRatingLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
    }

    public void transitionIn(Runnable runnable) {
        super.attach();
        playerSide.setLayoutX(-1920);
        opponentSide.setLayoutX(1920);

        Duration duration = Duration.seconds(0.7);

        Timeline playerTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(playerSide.layoutXProperty(), -1920)),// end value of rotate
                new KeyFrame(duration, new KeyValue(playerSide.layoutXProperty(), 0)) // initial rotate
        );

        Timeline opponentTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opponentSide.layoutXProperty(), 1920)),// end value of rotate
                new KeyFrame(duration, new KeyValue(opponentSide.layoutXProperty(), 0)) // initial rotate
        );

        playerTimeline.play();
        opponentTimeline.play();

        playerTimeline.setOnFinished(x -> {
            if (runnable != null) runnable.run();
        });
    }

    public void transitionOut(Runnable runnable) {
        super.attach();

        Duration duration = Duration.seconds(1.5);

        Timeline playerTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(playerSide.layoutXProperty(), 0)),// end value of rotate
                new KeyFrame(duration, new KeyValue(playerSide.layoutXProperty(), -1920)) // initial rotate
        );

        Timeline opponentTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opponentSide.layoutXProperty(), 0)),// end value of rotate
                new KeyFrame(duration, new KeyValue(opponentSide.layoutXProperty(), 1920)) // initial rotate
        );

        playerTimeline.play();
        opponentTimeline.play();

        playerTimeline.setOnFinished(x -> {
            if (runnable != null) runnable.run();
        });
    }

    public void setOpponentData(String name, int rating) {
        opponentNameLabel.setText(name);
        opponentRatingLabel.setText(String.valueOf(rating));
        playerNameLabel.setText(SessionManager.getInstance().getUsername());
        playerRatingLabel.setText(String.valueOf(SessionManager.getInstance().getRating()));
    }
}
