package me.logicologist.wordiple.server.match.round;

import me.logicologist.wordiple.server.user.WordipleUser;

public class StandardRound extends Round {

    public StandardRound(WordipleUser playerOne, WordipleUser playerTwo) {
        super();
        addPlayer(playerOne);
        addPlayer(playerTwo);
    }
}
