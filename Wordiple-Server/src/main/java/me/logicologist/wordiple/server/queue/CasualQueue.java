package me.logicologist.wordiple.server.queue;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CasualQueue extends Queue {

    private final HashMap<WordipleUser, Integer> rankDisparity;

    public CasualQueue() {
        super(QueueType.CASUAL);
        this.rankDisparity = new HashMap<>();
    }

    @Override
    public void onQueue(WordipleUser user) {
        this.rankDisparity.put(user, 0);
    }

    @Override
    public void onDequeue(WordipleUser user) {
        this.rankDisparity.remove(user);
    }

    @Override
    public void startQueueMatchmaker() {
        WordipleServer.getExecutor().scheduleAtFixedRate(() -> {
            for (WordipleUser queued : new ArrayList<>(super.inQueue)) {
                int queuedRating = queued.getRating();
                for (WordipleUser others : super.inQueue) {
                    if (queued == others) continue;
                    int otherRating = others.getRating();
                    if (otherRating >= queuedRating - this.rankDisparity.get(queued) && otherRating <= queuedRating + this.rankDisparity.get(queued) && queuedRating >= otherRating - this.rankDisparity.get(others) && queuedRating <= otherRating + this.rankDisparity.get(others)) {
                        super.inQueue.remove(queued);
                        super.inQueue.remove(others);
                        super.inGame.add(queued);
                        super.inGame.add(others);
                        // TODO: Start match
                        break;
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        WordipleServer.getExecutor().scheduleAtFixedRate(() -> {
            for (WordipleUser queued : new ArrayList<>(super.inQueue)) {
                this.rankDisparity.put(queued, this.rankDisparity.get(queued) + 1);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
