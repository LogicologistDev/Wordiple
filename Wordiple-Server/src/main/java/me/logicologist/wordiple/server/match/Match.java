package me.logicologist.wordiple.server.match;

import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.match.round.Round;
import me.logicologist.wordiple.server.packets.game.GameReadyPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Match<T extends Round> {

    protected final HashMap<WordipleUser, List<T>> score;
    protected final List<Round> rounds;
    protected final int winGoal;
    private final List<WordipleUser> readyUsers;


    public Match(int winGoal) {
        this.score = new HashMap<>();
        this.rounds = new ArrayList<>();
        this.readyUsers = new ArrayList<>();
        this.winGoal = winGoal;
    }

    public void addPlayer(WordipleUser user) {
        score.put(user, new ArrayList<>());
    }

    public void addRound(T t) {
        rounds.add(t);
    }

    public void startRound() {
        for (WordipleUser user : score.keySet()) {
            PacketManager.getInstance().getSocket().getPacket(GameReadyPacket.class).sendPacket(packet -> packet.getPacketType().getArguments(),
                    user.getOutputStream()
            );
        }
        startNewRound();
    }

    public List<WordipleUser> getPlayers() {
        return new ArrayList<>(score.keySet());
    }

    public void readyClient(WordipleUser user) {
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

    public Round getCurrentRound() {
        return rounds.get(rounds.size() - 1);
    }

    public abstract void startNewRound();
}