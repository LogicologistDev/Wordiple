package me.logicologist.wordiple.server.managers;

import me.logicologist.wordiple.server.match.Match;
import me.logicologist.wordiple.server.match.round.Round;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {

    private static MatchManager matchManager;
    private final List<Match<? extends Round>> matches;

    public MatchManager() {
        this.matches = new ArrayList<>();
        matchManager = this;
    }

    public void addMatch(Match<? extends Round> match) {
        matches.add(match);
    }

    public Match<? extends Round> getMatch(WordipleUser user) {
        return matches.stream().filter(x -> x.containsPlayer(user)).findFirst().orElse(null);
    }

    public void terminateMatch(Match<? extends Round> match) {
        matches.remove(match);
        QueueManager.getInstance().finalizeMatch(match);
    }

    public static MatchManager getInstance() {
        return matchManager;
    }


}
