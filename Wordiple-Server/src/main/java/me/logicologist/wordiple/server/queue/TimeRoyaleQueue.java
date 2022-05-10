package me.logicologist.wordiple.server.queue;

import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimeRoyaleQueue extends Queue {

    int startTimer;
    ScheduledFuture<?> startTimerFuture;

    public TimeRoyaleQueue() {
        super(QueueType.TIME_ROYALE);
    }

    @Override
    public void onQueue(WordipleUser user) {
    }

    @Override
    public void onDequeue(WordipleUser user) {
    }

    @Override
    public void startQueueMatchmaker() {
        WordipleServer.getExecutor().scheduleAtFixedRate(() -> {
            if (inQueue.size() >= 10) {
                List<WordipleUser> gameSent = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    gameSent.add(inQueue.remove(0));
                }
                inGame.addAll(gameSent);
                startTimerFuture.cancel(true);
                startTimerFuture = null;
                return;
            }
            if (inQueue.size() >= 4 && startTimerFuture == null) {
                startTimerFuture = WordipleServer.getExecutor().schedule(() -> {
                    List<WordipleUser> gameSent = new ArrayList<>();
                    for (int i = 0; i < Math.min(10, inQueue.size()); i++) {
                        gameSent.add(inQueue.remove(0));
                    }
                    inGame.addAll(gameSent);
                    startTimerFuture = null;
                }, 30, TimeUnit.SECONDS);
            }
            if (inQueue.size() < 4 && startTimerFuture != null) {
                startTimerFuture.cancel(true);
                startTimerFuture = null;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
