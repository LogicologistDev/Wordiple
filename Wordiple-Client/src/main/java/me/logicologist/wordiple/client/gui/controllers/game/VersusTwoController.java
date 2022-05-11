package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.manager.WordManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class VersusTwoController implements Initializable {

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
                if (charPattern.matcher(c).matches()) continue;
                verifiedString.append(c);
            }

            if (verifiedString.length() > 5) {
                playTextField.setText(verifiedString.substring(0, 5));
                playTextField.positionCaret(verifiedString.length());
            }

            setPlayerGuess(playTextField.getText());

            // Send packet to update board
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

    public void setOpponentGuess(int guess, String value) {
        AnchorPane guessRow = null;

        switch (guess) {
            case 1:
                guessRow = playerTwoRow1;
                break;
            case 2:
                guessRow = playerTwoRow2;
                break;
            case 3:
                guessRow = playerTwoRow3;
                break;
            case 4:
                guessRow = playerTwoRow4;
                break;
            case 5:
                guessRow = playerTwoRow5;
                break;
            case 6:
                guessRow = playerTwoRow6;
                break;
        }

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

    public void submitGuess(String value) {
        if (!WordManager.getInstance().isValid(value)) {
            new ShakeAnimation(1, getCurrentRow().layoutXProperty(), 100);
            return;
        }
        guessNumber++;
        playTextField.clear();

        // Send packet to submit
    }

    public void updateOpponentGuess(int guess, String codes) {

    }

    public List<Label> getGuessLabels(AnchorPane guessRow) {
        List<Label> labels = new ArrayList<>();

        guessRow.getChildren().forEach(x -> labels.add((Label) x));
        return labels;
    }

    public void setScore(int playerScore, int opponentScore) {
        this.playerScoreLabel.setText(String.valueOf(playerScoreLabel));
        this.opponentScoreLabel.setText(String.valueOf(opponentScoreLabel));
    }


}
