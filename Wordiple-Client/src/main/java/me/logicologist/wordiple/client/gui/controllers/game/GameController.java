package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.animations.BounceInAnimation;
import me.logicologist.wordiple.client.gui.animations.LetterFieldPopAnimation;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This abstract class is used by the common game controllers.
 * It is used to handle game data and send the appropriate packet(s) to the server.
 * This class is part of the game controller set.
 *
 * @author Logicologist
 * @since 1.0
 */
public abstract class GameController implements Initializable {


    @FXML
    protected TextField playTextField;

    @FXML
    protected AnchorPane lettersPane;

    @FXML
    protected AnchorPane timerPane;

    @FXML
    protected Label timerLabel;

    HashMap<String, AnchorPane> playerPanes = new HashMap<>();
    private ScheduledFuture<?> timerFuture = null;
    int guessNumber = 1;
    int maxGuesses = 6;


    public void setAnswerState(boolean locked) {
        this.playTextField.setDisable(locked);
    }

    public void setOpponentDisplay(String username, int guess, int length) {
        List<AnchorPane> anchorPane = getOpponentPanes(playerPanes.get(username));
        AnchorPane guessRow = anchorPane.get(guess - 1);

        List<Label> rowLabels = getGuessLabels(guessRow);

        for (int i = 0; i < 5; i++) {
            if (i < length) {
                rowLabels.get(i).setText("?");
                continue;
            }
            rowLabels.get(i).setText("");
        }
    }

    public void setPlayerGuessData(String username, int guess, String code) {
        List<AnchorPane> anchorPane = getOpponentPanes(playerPanes.get(username));
        AnchorPane guessRow = anchorPane.get(guess - 1);

        if (username.equals(SessionManager.getInstance().getUsername()) && guessNumber > guess) {
            Platform.runLater(() -> {
                for (int i = 0; i < code.length(); i++) {
                    Label label = getGuessLabels(guessRow).get(i);
                    System.out.println(guessRow + "GR");
                    System.out.println(label.getText() + "LAB");
                    char letter = label.getText().toUpperCase().charAt(0);
                    int id = letter - 65;
                    Label characterLabel = (Label) lettersPane.getChildren().get(id);
                    switch (code.charAt(i)) {
                        case 'c':
                            if (characterLabel.getStyleClass().contains("board-letter-default-correct")) continue;
                            characterLabel.getStyleClass().clear();
                            characterLabel.getStyleClass().add("board-letter-default-correct");
                            break;
                        case 'i':
                            if (characterLabel.getStyleClass().contains("board-letter-default-used")) continue;
                            if (characterLabel.getStyleClass().contains("board-letter-default-correct")) continue;
                            characterLabel.getStyleClass().clear();
                            characterLabel.getStyleClass().add("board-letter-default-used");
                            break;
                        case 'r':
                            if (characterLabel.getStyleClass().contains("board-letter-default-ready")) continue;
                            characterLabel.getStyleClass().clear();
                            characterLabel.getStyleClass().add("board-letter-default-ready");
                            break;
                    }
                    new LetterFieldPopAnimation(characterLabel, 1).play();
                }
            });
        }

        setRowData(guessRow, code);
    }

    public void setOpponentPane(String username, AnchorPane pane) {
        playerPanes.put(username, pane);
    }

    public List<AnchorPane> getOpponentPanes(AnchorPane anchorPane) {
        List<AnchorPane> panes = new ArrayList<>();

        anchorPane.getChildren().forEach(x -> {
            if (x instanceof AnchorPane) panes.add((AnchorPane) x);
        });
        return panes;
    }

    /**
     * This method retrieves the Labels of a AnchorPane representing a guess row.
     *
     * @param guessRow The AnchorPane representing the guess row.
     * @return A list of labels contained in the guess row in order.
     */
    public List<Label> getGuessLabels(AnchorPane guessRow) {
        List<Label> labels = new ArrayList<>();

        guessRow.getChildren().forEach(x -> labels.add((Label) x));
        return labels;
    }

    /**
     * This method is used to set the row's colors/data for a guess.
     * Received by the server as a packet to update the board.
     *
     * @param row  The row to set.
     * @param code The data to set.
     */
    public void setRowData(AnchorPane row, String code) {
        List<Label> rowLabels = getGuessLabels(row);

        for (int i = 0; i < 5; i++) {
            Label label = rowLabels.get(i);
            char c = code.charAt(i);
            WordipleClient.getExecutor().schedule(() -> {
                Platform.runLater(() -> {
                    switch (c) {
                        case 'r': // Ready
                            if (label.getStyleClass().contains("board-default-ready")) return;
                            label.getStyleClass().clear();
                            label.getStyleClass().add("board-default-ready");
                            break;
                        case 'c': // Correct
                            if (label.getStyleClass().contains("board-default-correct")) return;
                            label.getStyleClass().clear();
                            label.getStyleClass().add("board-default-correct");
                            break;
                        case 'i': // Incorrect
                            if (label.getStyleClass().contains("board-default-used")) return;
                            label.getStyleClass().clear();
                            label.getStyleClass().add("board-default-used");
                            break;
                        case 'u': // Unused
                            if (label.getStyleClass().contains("board-default-unused")) return;
                            label.getStyleClass().clear();
                            label.getStyleClass().add("board-default-unused");
                            break;
                        case 'l': // Locked
                            if (label.getStyleClass().contains("board-default-locked")) return;
                            label.getStyleClass().clear();
                            label.getStyleClass().add("board-default-locked");
                            break;
                    }
                    new LetterFieldPopAnimation(label, 1).play();
                });
            }, i * 40, TimeUnit.MILLISECONDS);
        }
    }

    public void startTimer(int time, int maxGuesses) {
        new BounceInAnimation(timerPane.layoutYProperty(), -100, 1).play();
        Platform.runLater(() -> {
            timerLabel.setText(time + "s");
        });
        AtomicInteger timer = new AtomicInteger(time);

        this.maxGuesses = maxGuesses;

        this.timerFuture = WordipleClient.getExecutor().scheduleAtFixedRate(() -> {
            if (timer.decrementAndGet() <= 0) {
                timerFuture.cancel(true);
                timerFuture = null;
                return;
            }
            Platform.runLater(() -> {
                timerLabel.setText(timer.get() + "s");
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}
