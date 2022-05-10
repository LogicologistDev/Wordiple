package me.logicologist.wordiple.server.managers;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.queue.CompetitiveQueue;
import me.logicologist.wordiple.server.queue.Queue;

import java.util.ArrayList;
import java.util.List;

public class QueueManager {

    private static QueueManager instance;

    private final List<Queue> queues;

    public QueueManager() {
        this.queues = new ArrayList<>();
    }

    public void load() {
        this.queues.add(new CompetitiveQueue());

        for (Queue queue : this.queues) {
            queue.startQueueInformer();
            queue.startQueueMatchmaker();
        }
    }

    public Queue getQueue(QueueType queueType) {
        return this.queues.stream().filter(x -> x.getQueueType().equals(queueType)).findFirst().orElse(null);
    }

    public static QueueManager getInstance() {
        return instance;
    }
}
