package me.logicologist.wordiple.client.gui.controllers.queue;

import me.logicologist.wordiple.common.queue.QueueType;

public class CasualQueueController extends QueueController {

    @Override
    public QueueType getQueueType() {
        return QueueType.CASUAL;
    }
}
