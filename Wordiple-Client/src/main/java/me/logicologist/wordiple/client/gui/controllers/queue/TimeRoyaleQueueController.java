package me.logicologist.wordiple.client.gui.controllers.queue;

import me.logicologist.wordiple.common.queue.QueueType;

public class TimeRoyaleQueueController extends QueueController {

    @Override
    public QueueType getQueueType() {
        return QueueType.TIME_ROYALE;
    }
}
