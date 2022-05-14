package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.animations.LetterFieldPopAnimation;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.manager.WordManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This class is used as the controller for 2 player versus screen.
 * It is used to handle the user input and send the appropriate packet(s) to the server.
 * This class is part of the game controller set.
 *
 * @author Logicologist
 * @since 1.0
 */
public class VersusTwoController extends GameController {

    @FXML
    private AnchorPane movablePane;

    @FXML
    private AnchorPane playerOnePane;

    @FXML
    private Label playerOneName;

    @FXML
    private Label playerTwoName;

    @FXML
    private AnchorPane playerOneRow1;

    @FXML
    private AnchorPane playerOneRow2;

    @FXML
    private AnchorPane playerOneRow3;

    @FXML
    private AnchorPane playerOneRow4;

    @FXML
    private AnchorPane playerOneRow5;

    @FXML
    private AnchorPane playerOneRow6;

    @FXML
    private AnchorPane playerTwoRow1;

    @FXML
    private AnchorPane playerTwoRow2;

    @FXML
    private AnchorPane playerTwoRow3;

    @FXML
    private AnchorPane playerTwoRow4;

    @FXML
    private AnchorPane playerTwoRow5;

    @FXML
    private AnchorPane playerTwoRow6;

    @FXML
    private Label playerScoreLabel;

    @FXML
    private Label opponentScoreLabel;

    @FXML
    private Label goalLabel;

    @FXML
    private TextField playTextField;

    int guessNumber = 1;
    int maxRows = 6;

    /**
     * The method run on initialization.
     * This method is overridden from the Initializable interface.
     *
     * @param url            The location of the FXML file.
     * @param resourceBundle The resources used by the FXML file.
     * @see javafx.fxml.Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Pattern charPattern = Pattern.compile("[a-zA-Z]");

        playTextField.setOnKeyTyped(e -> {
            if (guessNumber > 6) return;

            StringBuilder verifiedString = new StringBuilder();
            for (String c : playTextField.getText().split("")) {
                if (!charPattern.matcher(c).matches()) continue;
                verifiedString.append(c.toUpperCase());
            }

            playTextField.setText(verifiedString.toString());
            playTextField.positionCaret(verifiedString.length());

            if (verifiedString.length() > 5) {
                playTextField.setText(verifiedString.substring(0, 5));
                playTextField.positionCaret(verifiedString.length());
            }

            setPlayerGuess(playTextField.getText());

            // Send packet to update board
        });

        playTextField.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case ENTER:
                    submitGuess(playTextField.getText());
            }
        });
    }

    /**
     * This method retrieves the AnchorPane representing the current guess the player is on.
     *
     * @return The AnchorPane representing the current guess the player is on.
     */
    public AnchorPane getCurrentRow() {
        switch (guessNumber) {
            case 1:
                return playerOneRow1;
            case 2:
                return playerOneRow2;
            case 3:
                return playerOneRow3;
            case 4:
                return playerOneRow4;
            case 5:
                return playerOneRow5;
            case 6:
                return playerOneRow6;
        }
        return null;
    }

    /**
     * This method retrieves the AnchorPane representing the opponent's current guess.
     *
     * @return The AnchorPane representing the opponent's current guess.
     */
    public AnchorPane getOpponentRow(int row) {
        switch (row) {
            case 1:
                return playerTwoRow1;
            case 2:
                return playerTwoRow2;
            case 3:
                return playerTwoRow3;
            case 4:
                return playerTwoRow4;
            case 5:
                return playerTwoRow5;
            case 6:
                return playerTwoRow6;
        }
        return null;
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
                switch (c) {
                    case 'r': // Ready
                        label.getStyleClass().clear();
                        label.getStyleClass().add("board-default-ready");
                        break;
                    case 'c': // Correct
                        label.getStyleClass().clear();
                        label.getStyleClass().add("board-default-correct");
                        break;
                    case 'i': // Incorrect
                        label.getStyleClass().clear();
                        label.getStyleClass().add("board-default-used");
                        break;
                    case 'u': // Unused
                        label.getStyleClass().clear();
                        label.getStyleClass().add("board-default-unused");
                        break;
                    case 'l': // Locked
                        label.getStyleClass().clear();
                        label.getStyleClass().add("board-default-locked");
                        break;
                }
                label.setText("");
                new LetterFieldPopAnimation(label, 1).play();
            }, i * 40, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * This method is used to set the player's current guess
     * It is used to display to the player their current guess.
     *
     * @param value The string of their guess
     */
    public void setPlayerGuess(String value) {
        AnchorPane guessRow = getCurrentRow();

        if (guessRow == null) return;

        List<Label> rowLabels = getGuessLabels(guessRow);

        for (int i = 0; i < 5; i++) {
            if (i < value.length()) {
                rowLabels.get(i).setText(String.valueOf(value.charAt(i)));
                continue;
            }
            rowLabels.get(i).setText("");
        }
    }

    /**
     * This method is used to set the player's data for a guess.
     * Received by the server as a packet to update the board.
     *
     * @param row   The row to set.
     * @param value The data to set for that row.
     */
    public void updatePlayerGuess(int row, String value) {
        AnchorPane guessRow = getCurrentRow();

        if (guessRow == null) return;

        setRowData(guessRow, value);
    }

    /**
     * This method is used to sumbit the current guess the player has made.
     * <p>
     * Before submitting as a packet to the server, the player's guess is validated through WordManager.
     *
     * @param value The player's guess
     * @see WordManager
     * @see WordManager#isValid(String)
     */
    public void submitGuess(String value) {
        if (getCurrentRow() == null) return;
        if (!WordManager.getInstance().isValid(value)) {
            new ShakeAnimation(1, getCurrentRow().layoutYProperty(), 100).play();
            return;
        }
        guessNumber++;
        playTextField.clear();
        updatePlayerGuess(guessNumber, "rrrrr");
        // Send packet to submit
    }

    /**
     * This method is used to set the opponent's current guess.
     * It is used to display to the player their opponent's current guess.
     * Prevents showing the actual content of their guess.
     *
     * @param guess Their current guess number.
     * @param length The length of their guess
     */

    public void setOpponentGuess(int guess, int length) {
        AnchorPane guessRow = getOpponentRow(guess);

        if (guessRow == null) return;

        List<Label> rowLabels = getGuessLabels(guessRow);

        for (int i = 0; i < 5; i++) {
            if (i < length) {
                rowLabels.get(i).setText("?");
                continue;
            }
            rowLabels.get(i).setText("");
        }
    }

    /**
     * This method is used to set the opponent's data for a guess.
     * Received by the server as a packet to update the board.
     *
     * @param guess The guess to set.
     * @param code  The data to set.
     */
    public void updateOpponentGuess(int guess, String code) {
        AnchorPane guessRow = getOpponentRow(guess);

        if (guessRow == null) return;

        setRowData(guessRow, code);
    }

    /**
     * This method is used to set the current score of the entire match.
     * Received by the server as a packet to update the board.
     *
     * @param playerScore The player's current score.
     * @param opponentScore The opponent's current score.
     */
    public void setScore(int playerScore, int opponentScore) {
        this.playerScoreLabel.setText(String.valueOf(playerScoreLabel));
        this.opponentScoreLabel.setText(String.valueOf(opponentScoreLabel));
    }

    /**
     * This method is used to set the current end goal of the game.
     * This is typically going to be Casual or Competitive, due to the nature of 2 player matches.
     *
     * @param goal The current end goal of the game.
     * @param opponentName The opponent's name.
     */
    public void setGameMeta(String goal, String opponentName) {
        this.goalLabel.setText(goal);
        this.playerOneName.setText(SessionManager.getInstance().getUsername());
        this.playerTwoName.setText(opponentName);
    }
}
