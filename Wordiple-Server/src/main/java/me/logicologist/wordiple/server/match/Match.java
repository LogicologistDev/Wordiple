package me.logicologist.wordiple.server.match;

import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.QueueManager;
import me.logicologist.wordiple.server.match.round.Round;
import me.logicologist.wordiple.server.packets.game.GameOverlayPacket;
import me.logicologist.wordiple.server.packets.game.GameReadyPacket;
import me.logicologist.wordiple.server.packets.game.UpdateScoreboardPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Match<T extends Round> {

    protected final HashMap<WordipleUser, List<T>> score;
    protected final List<T> rounds;
    protected final int winGoal;
    private final List<WordipleUser> readyUsers;
    private final long matchStartTime;


    public Match(int winGoal) {
        this.score = new HashMap<>();
        this.rounds = new ArrayList<>();
        this.readyUsers = new ArrayList<>();
        this.winGoal = winGoal;
        this.matchStartTime = System.currentTimeMillis();
    }

    public void addPlayer(WordipleUser user) {
        score.put(user, new ArrayList<>());
    }

    public void addRound(T t) {
        rounds.add(t);
    }

    public void startRound() {
        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : score.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(GameOverlayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                .setValues("display", "Ready...")
                                .setValues("displayTime", "3"),
                        user.getOutputStream()
                );
            }
        }, 1, TimeUnit.SECONDS);

        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : score.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(GameOverlayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                .setValues("display", "Set...")
                                .setValues("displayTime", "2"),
                        user.getOutputStream()
                );
            }
        }, 2, TimeUnit.SECONDS);

        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : score.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(GameOverlayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                .setValues("display", "Go!")
                                .setValues("displayTime", "1"),
                        user.getOutputStream()
                );
                PacketManager.getInstance().getSocket().getPacket(GameReadyPacket.class).sendPacket(packet -> packet.getPacketType().getArguments(),
                        user.getOutputStream()
                );
            }
            startNewRound();
        }, 3, TimeUnit.SECONDS);
    }

    public List<WordipleUser> getPlayers() {
        return new ArrayList<>(score.keySet());
    }

    public void readyClient(WordipleUser user) {
        QueueManager.getInstance().removeAllQueueViewers(user);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        readyUsers.add(user);
        if (readyUsers.size() == score.size()) {
            startRound();
            readyUsers.clear();
        }
    }

    public void terminateMatch() {

    }

    public boolean containsPlayer(WordipleUser user) {
        return score.containsKey(user);
    }

    public T getCurrentRound() {
        return rounds.get(rounds.size() - 1);
    }

    public void setRoundWinner(WordipleUser winner) {
        if (winner == null) return;
        score.get(winner).add(getCurrentRound());
        if (score.get(winner).size() == winGoal) {
            terminateMatch();
        }
        for (WordipleUser user : score.keySet()) {
            for (WordipleUser scoreUser : score.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(UpdateScoreboardPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                .setValues("player", scoreUser.getUsername())
                                .setValues("score", score.get(scoreUser).size()),
                        user.getOutputStream()
                );
            }
        }
    }

    public abstract void startNewRound();
}
