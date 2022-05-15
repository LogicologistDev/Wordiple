package me.logicologist.wordiple.client.gui.controllers.game;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.animations.ShakeAnimation;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.manager.WordManager;
import me.logicologist.wordiple.client.packets.game.GuessWordPacket;
import me.logicologist.wordiple.client.packets.game.UpdateDisplayPacket;
import me.logicologist.wordiple.common.packets.AuthPacketType;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
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
    private AnchorPane playerTwoPane;

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
    private Label playerScoreLabel;

    @FXML
    private Label opponentScoreLabel;

    @FXML
    private Label goalLabel;

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

        AtomicReference<String> previousField = new AtomicReference<>();
        playTextField.setOnKeyTyped(e -> {
            if (guessNumber > 6 || playTextField.getText().equals(previousField.get())) return;

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
            previousField.set(playTextField.getText());
            setPlayerGuess(playTextField.getText());

            PacketManager.getInstance().getSocket().getPacket(UpdateDisplayPacket.class).sendPacket(packet -> packet
                    .getPacketType(AuthPacketType.class)
                    .getArguments(SessionManager.getInstance().getLocalSessionID())
                    .setValues("name", SessionManager.getInstance().getUsername())
                    .setValues("text", playTextField.getText().toUpperCase())
            );
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
        if (guessNumber <= maxGuesses) super.setPlayerGuessData(SessionManager.getInstance().getUsername(), guessNumber, "rrrrr");
        PacketManager.getInstance().getSocket().getPacket(GuessWordPacket.class).sendPacket(packet -> packet
                .getPacketType(AuthPacketType.class)
                .getArguments(SessionManager.getInstance().getLocalSessionID())
                .setValues("word", value)
        );
        if (guessNumber > maxGuesses) {
            super.setAnswerLocked(true);
        }
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
        super.setPlayerPane(SessionManager.getInstance().getUsername(), playerOnePane);
        super.setPlayerPane(opponentName, playerTwoPane);
        super.setPlayerScoreLabels(SessionManager.getInstance().getUsername(), playerScoreLabel);
        super.setPlayerScoreLabels(opponentName, opponentScoreLabel);
    }
}
