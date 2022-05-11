package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    public List<Label> getGuessLabels(AnchorPane guessRow) {
        List<Label> labels = new ArrayList<>();

        guessRow.getChildren().forEach(x -> labels.add((Label) x));
        return labels;
    }

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

    public void updatePlayerGuess(String value) {
        AnchorPane guessRow = getCurrentRow();

        if (guessRow == null) return;

        setRowData(guessRow, value);
    }

    public void submitGuess(String value) {
        if (!WordManager.getInstance().isValid(value)) {
            new ShakeAnimation(1, getCurrentRow().layoutXProperty(), 100).play();
            return;
        }
        guessNumber++;
        playTextField.clear();
        updatePlayerGuess("rrrrr");
        // Send packet to submit
    }

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

    public void updateOpponentGuess(int guess, String codes) {
        AnchorPane guessRow = getOpponentRow(guess);

        if (guessRow == null) return;

        setRowData(guessRow, codes);
    }

    public void setScore(int playerScore, int opponentScore) {
        this.playerScoreLabel.setText(String.valueOf(playerScoreLabel));
        this.opponentScoreLabel.setText(String.valueOf(opponentScoreLabel));
    }

    public void setGameMeta(String goal, String opponentName) {
        this.goalLabel.setText(goal);
        this.playerOneName.setText(SessionManager.getInstance().getUsername());
        this.playerTwoName.setText(opponentName);
    }
}
