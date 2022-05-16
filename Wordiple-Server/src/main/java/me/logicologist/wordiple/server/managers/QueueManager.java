package me.logicologist.wordiple.server.managers;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.match.Match;
import me.logicologist.wordiple.server.match.round.Round;
import me.logicologist.wordiple.server.queue.CasualQueue;
import me.logicologist.wordiple.server.queue.CompetitiveQueue;
import me.logicologist.wordiple.server.queue.Queue;
import me.logicologist.wordiple.server.queue.TimeRoyaleQueue;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.List;

public class QueueManager {

    private static QueueManager instance;

    private final List<Queue> queues;

    public QueueManager() {
        this.queues = new ArrayList<>();
        instance = this;
    }

    public void load() {
        this.queues.add(new CompetitiveQueue());
        this.queues.add(new CasualQueue());
        this.queues.add(new TimeRoyaleQueue());

        for (Queue queue : this.queues) {
            queue.startQueueInformer();
            System.out.println("Loading queue " + queue.getQueueType());
            queue.startQueueMatchmaker();
        }
    }

    public Queue getQueue(QueueType queueType) {
        return this.queues.stream().filter(x -> x.getQueueType().equals(queueType)).findFirst().orElse(null);
    }

    public void removeFromAllQueues(WordipleUser user) {
        for (Queue queue : this.queues) {
            queue.dequeue(user);
        }
    }

    public boolean isPlayerActive(WordipleUser user) {
        for (Queue queue : this.queues) {
            if (queue.isActive(user)) {
                return true;
            }
        }
        return false;
    }

    public void removeAllQueueViewers(WordipleUser user) {
        for (Queue queue : this.queues) {
            queue.removeQueueViewer(user);
        }
    }

    public void finalizeMatch(Match<? extends Round> match) {
        Queue queue = getQueue(match.getGameType());
        for (WordipleUser player : match.getPlayers()) {
            queue.removeInGame(player);
        }
    }

    public static QueueManager getInstance() {
        return instance;
    }
}
