package me.logicologist.wordiple.server.match;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.match.round.StandardRound;
import me.logicologist.wordiple.server.packets.game.GameEndPacket;
import me.logicologist.wordiple.server.packets.game.GameStartPacket;
import me.logicologist.wordiple.server.rank.RankRange;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CompetitiveMatch extends Match<StandardRound> {

    public CompetitiveMatch(WordipleUser playerOne, WordipleUser playerTwo) {
        super(Math.max(RankRange.getInstance().getRank(playerOne.getRating()).getCompetitiveRounds(), RankRange.getInstance().getRank(playerTwo.getRating()).getCompetitiveRounds()), QueueType.COMPETITIVE);

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

    @Override
    public void terminateMatch(WordipleUser winner) {
        WordipleUser loser = super.getPlayers().stream().filter(x -> x != winner).findFirst().orElse(null);

        MatchManager.getInstance().terminateMatch(this);

        int rounds = super.rounds.size();

        double ratingWon = loser == null ? 0 : 10;
        double ratingLost = 0;

        if (loser != null) {

            // Give/take rating based on performance.
            // Take into account:
            // - Guess ratio (Sweeps)
            // - Guess efficiency
            // - Solve speed
            // - Opponent difficulty
            double winnerSolveSpeed = super.getAverageSolveTime(winner);
            double loserSolveSpeed = super.getAverageSolveTime(loser);
            double winnerGuesses = super.getAverageGuesses(winner);
            double loserGuesses = super.getAverageGuesses(loser);
            long winnerUnsolved = super.getAmountUnsolved(winner);
            long loserUnsolved = super.getAmountUnsolved(loser);
            // If the winner has a lower rating than the loser, give them a sqrt point boost
            // If the loser has over 208 higher rating, penalize for boosting
            if (winner.getRating() < loser.getRating()) {
                int disparity = loser.getRating() - winner.getRating();
                ratingWon += Math.sqrt(disparity);
                ratingLost += Math.pow(disparity, 2) / 3000;
            }
            // If the winner has a higher rating, cap the bonus they can get at 196 rating. Above that means that the players are way out of range, and should not be rewarded or penalized.
            if (loser.getRating() < winner.getRating()) {
                int disparity = winner.getRating() - loser.getRating();
                double difference = Math.max(0, -1 * Math.sqrt(disparity) + 14);
                ratingWon += difference;
                ratingLost += difference;
            }
            // If the winner solves faster than the loser, give them a bonus if they are at most 100 rating above the loser multiplied by every 5 seconds
            int disparity = loser.getRating() - winner.getRating();
            if (winnerSolveSpeed > loserSolveSpeed && disparity > -100) {
                double bonus = (((winnerSolveSpeed - loserSolveSpeed) / 5) * (Math.sqrt(disparity + 100) / 10));
                ratingWon += bonus;
                ratingLost += bonus / 2;
            }
            // If the winner guesses more efficiently, give them a bonus if they are at most 100 rating above the loser by the difference multiplied by 20, and loser difference multiplied by 14. (1 difference is HUGE, the player SHOULD rankup)
            if (loserGuesses > winnerGuesses && disparity > -100) {
                double difference = loserGuesses - winnerGuesses;
                ratingWon += difference * 20;
                ratingLost += difference * 14;
            }
            // A sweep will guarantee a higher bonus and higher loss, and vice versa. The closer to x-0, the better for the winning.
            if (disparity > -100) {
                long matchCloseness = loserUnsolved - winnerUnsolved;
                ratingWon += Math.pow((((double) (rounds - winnerUnsolved)) / Math.max(1, winnerUnsolved)) + 1, matchCloseness);
                ratingLost += Math.pow((((double) loserUnsolved) / Math.max(1, rounds - loserUnsolved)) + 1, matchCloseness);
            }

            winner.setRating(winner.getRating() + (int) ratingWon);
            loser.setRating(loser.getRating() - (int) ratingLost);
        }

        // Give experience for longer and closer games, give less for shorter and further games.
        int secondsPlayed = (int) ((System.currentTimeMillis() - super.matchStartTime) / 1000);
        int experience = secondsPlayed / 30 * Math.min(rounds, super.winGoal * 2);

        winner.setExperience(winner.getExperience() + experience);
        loser.setExperience(loser.getExperience() + experience);

        winner.setWins(winner.getWins() + 1);

        int finalRatingWon = (int) ratingWon;
        int finalRatingLost = (int) ratingLost;
        WordipleServer.getExecutor().schedule(() -> {
            PacketManager.getInstance().getSocket().getPacket(GameEndPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("type", QueueType.COMPETITIVE)
                            .setValues("winner", winner.getUsername())
                            .setValues("scoredisplay", super.score.get(winner).size() + "-" + super.score.get(loser).size())
                            .setValues("rating", winner.getRating())
                            .setValues("ratingchange", finalRatingWon)
                            .setValues("rank", RankRange.getInstance().getRank(winner.getRating()).getName())
                            .setValues("ratingtorankup", RankRange.getInstance().getRatingToNextRank(winner.getRating()))
                            .setValues("newexperience", winner.getExperience())
                            .setValues("newlevel", winner.getLevel())
                            .setValues("requiredexperience", winner.getNeededExperience()),
                    winner.getOutputStream()
            );

            PacketManager.getInstance().getSocket().getPacket(GameEndPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("type", QueueType.COMPETITIVE)
                            .setValues("winner", winner.getUsername())
                            .setValues("scoredisplay", super.score.get(loser).size() + "-" + super.score.get(winner).size())
                            .setValues("rating", loser.getRating())
                            .setValues("ratingchange", -1 * finalRatingLost)
                            .setValues("rank", RankRange.getInstance().getRank(loser.getRating()).getName())
                            .setValues("ratingtorankup", RankRange.getInstance().getRatingToNextRank(loser.getRating()))
                            .setValues("newexperience", loser.getExperience())
                            .setValues("newlevel", loser.getLevel())
                            .setValues("requiredexperience", loser.getNeededExperience()),
                    loser.getOutputStream()
            );
        }, 8, TimeUnit.SECONDS);

    }
}
