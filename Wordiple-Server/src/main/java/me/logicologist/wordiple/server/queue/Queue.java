package me.logicologist.wordiple.server.queue;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.packets.info.QueueInfoPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Queue {

    protected final List<WordipleUser> inQueue;
    protected final List<WordipleUser> inGame;
    protected final List<WordipleUser> viewingQueue;
    private final QueueType queueType;

    public Queue(QueueType queueType) {
        this.inQueue = new ArrayList<>();
        this.inGame = new ArrayList<>();
        this.viewingQueue = new ArrayList<>();
        this.queueType = queueType;
    }

    public void startQueueInformer() {
        WordipleServer.getExecutor().scheduleAtFixedRate(() -> {
            for (WordipleUser user : viewingQueue) {
                PacketManager.getInstance().getSocket().getPacket(QueueInfoPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                .setValues("active", getActive())
                                .setValues("queuetype", queueType),
                        user.getOutputStream()
                );
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void queue(WordipleUser user) {
        this.inQueue.add(user);
    }

    public void dequeue(WordipleUser user) {
        this.inQueue.remove(user);
    }

    public void addQueueViewer(WordipleUser user) {
        this.viewingQueue.add(user);
    }

    public void removeQueueViewer(WordipleUser user) {
        this.viewingQueue.remove(user);
    }

    public int getActive() {
        return this.inQueue.size() + this.inGame.size();
    }

    public abstract void onQueue(WordipleUser user);

    public abstract void onDequeue(WordipleUser user);

    public abstract void startQueueMatchmaker();

    public QueueType getQueueType() {
        return queueType;
    }

}
