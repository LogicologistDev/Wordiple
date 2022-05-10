package me.logicologist.wordiple.client.gui.controllers.queue;

import com.olziedev.olziesocket.framework.action.SocketActionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.gui.controllers.transitions.FadeHorizontalTransitionAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.packets.queue.EnterQueuePacket;
import me.logicologist.wordiple.client.packets.queue.LeaveQueuePacket;
import me.logicologist.wordiple.client.packets.queue.QueueInfoRegisterPacket;
import me.logicologist.wordiple.client.packets.queue.QueueInfoUnregisterPacket;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.common.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class QueueController extends FadeHorizontalTransitionAdapter {
    @FXML
    protected AnchorPane movablePane;

    @FXML
    protected Button enterButton;

    @FXML
    protected Button backButton;

    @FXML
    protected Label timeLabel;

    @FXML
    protected Label activeLabel;

    private String queueStyle;
    private String dequeueStyle;

    public boolean midAction = false;
    private ScheduledFuture<?> future = null;
    private boolean inQueue = false;
    private QueueType queueType = null;

    public void setActive(int active) {
        Platform.runLater(() -> {
            activeLabel.setText(active + " Active");
        });
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        queueType = getQueueType();
        super.setPane(movablePane);

        PacketManager.getInstance().getSocket().getPacket(QueueInfoRegisterPacket.class).sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID())
                .setValues("queuetype", queueType)
        ).waitForResponse(packet -> false, () -> {
            Platform.runLater(() -> {
                LoadScreenController failController = GUIManager.getInstance().showLoadScreen("Failed to fetch queue data.");
                WordipleClient.getExecutor().schedule(() -> {
                    failController.remove(() -> {
                        super.transitionOut(() -> {
                            PacketManager.getInstance().getSocket().getActionRegister().executeAction(SocketActionType.CONNECTION_LOST, null);
                        });
                    });
                }, 2, TimeUnit.SECONDS);
            });
        });
        Runnable runnable = () -> {
            if (midAction) return;
            midAction = true;

            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Releasing Queue Data...");

            PacketManager.getInstance().getSocket().getPacket(QueueInfoUnregisterPacket.class).sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID())
                    .setValues("queuetype", queueType)
            ).waitForResponse(response1 -> {
                loadScreenController.remove(() -> {
                    if (future != null) future.cancel(true);
                    if (inQueue) {
                        dequeue(() -> {
                            super.transitionOut(() -> {
                                GUIManager.getInstance().showGameSelectScreen(true);
                            });
                        });
                        return;
                    }

                    super.transitionOut(() -> {
                        GUIManager.getInstance().showGameSelectScreen(true);
                    });
                });
                return false;
            }, () -> Platform.runLater(() -> {
                LoadScreenController failController = GUIManager.getInstance().showLoadScreen("Failed to release queue data.");
                WordipleClient.getExecutor().schedule(() -> {
                    failController.remove(() -> {
                        super.transitionOut(() -> {
                            PacketManager.getInstance().getSocket().getActionRegister().executeAction(SocketActionType.CONNECTION_LOST, null);
                        });
                    });
                }, 2, TimeUnit.SECONDS);
            }));
        };
        backButton.setOnAction(event -> {
            runnable.run();
        });

        enterButton.setOnAction(event -> {
            if (midAction) return;
            midAction = true;

            if (!inQueue) {
                LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Joining Queue...");

                PacketManager.getInstance().getSocket().getPacket(EnterQueuePacket.class).sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID())
                        .setValues("queuetype", queueType)
                ).waitForResponse(packet -> {
                    Platform.runLater(() -> {
                        inQueue = true;
                        enterButton.getStyleClass().clear();
                        enterButton.getStyleClass().add(dequeueStyle);
                        this.startTimer();
                        loadScreenController.remove(null);
                    });
                    return false;
                }, () -> {
                    Platform.runLater(() -> {
                        LoadScreenController failController = GUIManager.getInstance().showLoadScreen("Failed to join queue.");
                        WordipleClient.getExecutor().schedule(() -> {
                            failController.remove(() -> {
                                PacketManager.getInstance().getSocket().getActionRegister().executeAction(SocketActionType.CONNECTION_LOST, null);
                            });
                        }, 2, TimeUnit.SECONDS);
                    });
                });
                return;
            }
            dequeue(() -> midAction = false);
        });

        movablePane.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    runnable.run();
                    return;
            }
        });
    }

    public void startTimer() {
        midAction = false;
        AtomicLong openedTime = new AtomicLong();
        future = WordipleClient.getExecutor().scheduleAtFixedRate(() -> {
            Platform.runLater(() -> timeLabel.setText("Queue Elapsed: " + Utils.formatShortTime(openedTime.getAndIncrement())));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopTimer() {
        future.cancel(true);
        timeLabel.setText("Not in queue!");
        midAction = false;
    }

    public void setQueueButtonStyles(String queue, String dequeue) {
        this.queueStyle = queue;
        this.dequeueStyle = dequeue;
    }

    private void dequeue(Runnable runnable) {
        Platform.runLater(() -> {
            LoadScreenController loadScreenController = GUIManager.getInstance().showLoadScreen("Leaving Queue...");

            PacketManager.getInstance().getSocket().getPacket(LeaveQueuePacket.class).sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID())
                    .setValues("queuetype", queueType)
            ).waitForResponse(packet -> {
                Platform.runLater(() -> {
                    enterButton.getStyleClass().clear();
                    enterButton.getStyleClass().add(queueStyle);
                    this.stopTimer();
                    loadScreenController.remove(() -> {
                        if (runnable != null) runnable.run();
                    });
                });
                return false;
            }, () -> {
                Platform.runLater(() -> {
                    LoadScreenController failController = GUIManager.getInstance().showLoadScreen("Failed to leave queue.");
                    WordipleClient.getExecutor().schedule(() -> {
                        failController.remove(() -> {
                            midAction = false;
                        });
                    }, 2, TimeUnit.SECONDS);
                });
            });
        });
    }

    public abstract QueueType getQueueType();
}
