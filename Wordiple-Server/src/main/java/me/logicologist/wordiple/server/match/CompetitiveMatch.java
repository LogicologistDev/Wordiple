package me.logicologist.wordiple.server.match;

import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.match.round.StandardRound;
import me.logicologist.wordiple.server.packets.game.GameReadyPacket;
import me.logicologist.wordiple.server.packets.game.GameStartPacket;
import me.logicologist.wordiple.server.rank.RankRange;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompetitiveMatch extends Match<StandardRound> {

    public CompetitiveMatch(WordipleUser playerOne, WordipleUser playerTwo) {
        super(Math.max(RankRange.getInstance().getRank(playerOne.getRank()).getCompetitiveRounds(), RankRange.getInstance().getRank(playerTwo.getRank()).getCompetitiveRounds()));

        super.addPlayer(playerOne);
        super.addPlayer(playerTwo);
    }

    public void match() {
        List<WordipleUser> players = super.getPlayers();
        String goal = "Competitive: First-to-" + super.winGoal;
        PacketManager.getInstance().getSocket().getPacket(GameStartPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                .setValues("type", QueueType.COMPETITIVE)
                .setValues("opponent", players.get(1).getUsername())
                .setValues("rating", players.get(1).getRating())
                .setValues("goal", goal),
                players.get(0).getOutputStream()
        );
        PacketManager.getInstance().getSocket().getPacket(GameStartPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                .setValues("type", QueueType.COMPETITIVE)
                .setValues("opponent", players.get(0).getUsername())
                .setValues("rating", players.get(0).getRating())
                .setValues("goal", goal),
                players.get(1).getOutputStream()
        );
    }

    @Override
    public void startNewRound() {
        List<WordipleUser> players = super.getPlayers();
        super.addRound(new StandardRound(players.get(0), players.get(1)));
    }

    public void finalizeMatch() {
        // Give/take rating based on performance.
        // Take into account:
        // - Avg guesses/round
        // - Avg solve speed
        // - Disparity between players
    }
}
