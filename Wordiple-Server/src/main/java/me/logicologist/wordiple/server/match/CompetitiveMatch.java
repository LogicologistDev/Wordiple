package me.logicologist.wordiple.server.match;

import me.logicologist.wordiple.server.match.round.StandardRound;
import me.logicologist.wordiple.server.rank.RankRange;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompetitiveMatch {

    private final HashMap<WordipleUser, List<StandardRound>> score; // Used to show who won a round in replay, scores can be calculated by checking size of list.
    private final List<StandardRound> rounds; // Used to store the order of rounds played, and the current round. If the certain round does not appear in the score, it was a draw. Stored for replay purposes.
    private final int winGoal;

    public CompetitiveMatch(WordipleUser playerOne, WordipleUser playerTwo) {
        this.score = new HashMap<>();
        this.rounds = new ArrayList<>();

        this.score.put(playerOne, new ArrayList<>());
        this.score.put(playerTwo, new ArrayList<>());

        RankRange rankRange = RankRange.getInstance();
        this.winGoal = Math.max(rankRange.getRank(playerOne.getRank()).getCompetitiveRounds(), rankRange.getRank(playerTwo.getRank()).getCompetitiveRounds());
    }

    public void startNewRound() {
        List<WordipleUser> players = new ArrayList<>(score.keySet());
        rounds.add(new StandardRound(players.get(0), players.get(1)));
    }

    public void finalizeMatch() {
        // Give/take rating based on performance.
        // Take into account:
        // - Avg guesses/round
        // - Avg solve speed
        // - Disparity between players
    }

}
