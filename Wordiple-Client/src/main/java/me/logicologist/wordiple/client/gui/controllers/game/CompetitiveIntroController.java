package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.AttachableAdapter;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * This class is used as the controller for the competitive intro screen.
 * It is used to handle the transition between the main menu and game.
 * This class is part of the game controller set.
 *
 * @author      Logicologist
 * @since       1.0
 */
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

    /**
     * The method run on initialization.
     * This method is overridden from the Initializable interface.
     *
     * @see javafx.fxml.Initializable
     * @param url The location of the FXML file.
     * @param resourceBundle The resources used by the FXML file.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.setAttachment(movablePane);
    }

    /**
     * This method is called when the competitive intro screen is shown.
     * It uses the AttachableAdapter class to attach the movablePane to the main screen.
     *
     * On animation completion, the callback is called.
     *
     * @see me.logicologist.wordiple.client.gui.controllers.AttachableAdapter
     * @param runnable The callback to be called when the animation is complete.
     */
    public void transitionIn(Runnable runnable) {
        super.attach();
        playerSide.setLayoutX(-1920);
        opponentSide.setLayoutX(1920);

        Duration duration = Duration.seconds(0.7);

        Timeline playerTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(playerSide.layoutXProperty(), -1920)),
                new KeyFrame(duration, new KeyValue(playerSide.layoutXProperty(), 0))
        );

        Timeline opponentTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opponentSide.layoutXProperty(), 1920)),
                new KeyFrame(duration, new KeyValue(opponentSide.layoutXProperty(), 0))
        );

        playerTimeline.play();
        opponentTimeline.play();

        playerTimeline.setOnFinished(x -> {
            if (runnable != null) runnable.run();
        });
    }

    /**
     * This method is called when the competitive intro screen is hidden.
     * It uses the AttachableAdapter class to attach and detach the movablePane from the main screen.
     *
     * On animation completion, the callback is called.
     *
     * @see me.logicologist.wordiple.client.gui.controllers.AttachableAdapter
     * @param runnable The callback to be called when the animation is complete.
     */
    public void transitionOut(Runnable runnable) {
        super.attach();
        Duration duration = Duration.seconds(1.5);

        Timeline playerTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(playerSide.layoutXProperty(), 0)),
                new KeyFrame(duration, new KeyValue(playerSide.layoutXProperty(), -1920))
        );

        Timeline opponentTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opponentSide.layoutXProperty(), 0)),
                new KeyFrame(duration, new KeyValue(opponentSide.layoutXProperty(), 1920))
        );

        WordipleClient.getExecutor().schedule(() -> {
            playerTimeline.play();
            opponentTimeline.play();
        }, 1, TimeUnit.SECONDS);


        playerTimeline.setOnFinished(x -> {
            super.detach();
            if (runnable != null) runnable.run();
        });
    }

    /**
     * This method is called when the competitive intro screen is shown.
     * It sets the player and opponents' name and rating on the screen.
     *
     * @param name The opponents' name.
     * @param rating The opponents' rating.
     */
    public void setOpponentData(String name, int rating) {
        opponentNameLabel.setText(name);
        opponentRatingLabel.setText(rating + " WR");
        playerNameLabel.setText(SessionManager.getInstance().getUsername());
        playerRatingLabel.setText(SessionManager.getInstance().getRating() + " WR");
    }
}
