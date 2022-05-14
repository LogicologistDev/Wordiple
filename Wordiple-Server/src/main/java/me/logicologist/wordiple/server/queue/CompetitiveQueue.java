package me.logicologist.wordiple.server.queue;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.match.CompetitiveMatch;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CompetitiveQueue extends Queue {

    private final HashMap<WordipleUser, Integer> ratingDisparity;

    public CompetitiveQueue() {
        super(QueueType.COMPETITIVE);
        this.ratingDisparity = new HashMap<>();
    }

    @Override
    public void onQueue(WordipleUser user) {
        this.ratingDisparity.put(user, 0);
    }

    @Override
    public void onDequeue(WordipleUser user) {
        this.ratingDisparity.remove(user);
    }

    @Override
    public void startQueueMatchmaker() {
        WordipleServer.getExecutor().scheduleAtFixedRate(() -> {
            try {
                for (WordipleUser queued : new ArrayList<>(super.inQueue)) {
                    this.ratingDisparity.put(queued, this.ratingDisparity.get(queued) + 5);
                    int queuedRating = queued.getRating();
                    for (WordipleUser others : super.inQueue) {
                        if (queued == others) continue;
                        int otherRating = others.getRating();
                        if (otherRating >= queuedRating - this.ratingDisparity.get(queued) && otherRating <= queuedRating + this.ratingDisparity.get(queued) && queuedRating >= otherRating - this.ratingDisparity.get(others) && queuedRating <= otherRating + this.ratingDisparity.get(others)) {
                            super.inQueue.remove(queued);
                            super.inQueue.remove(others);
                            super.inGame.add(queued);
                            super.inGame.add(others);
                            CompetitiveMatch match = new CompetitiveMatch(queued, others);
                            match.match();
                            MatchManager.getInstance().addMatch(match);
                            new CompetitiveMatch(queued, others);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
