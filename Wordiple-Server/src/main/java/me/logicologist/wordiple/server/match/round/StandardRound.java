package me.logicologist.wordiple.server.match.round;

import me.logicologist.wordiple.server.user.WordipleUser;

public class StandardRound extends Round {


//    private final List<> replay; need to add some replay packet or something for replays

    public StandardRound(WordipleUser playerOne, WordipleUser playerTwo) {
        super();
        addPlayer(playerOne);
        addPlayer(playerTwo);
    }
}
