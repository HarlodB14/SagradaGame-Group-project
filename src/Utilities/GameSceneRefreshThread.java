package Utilities;

import Controller.GameController;
import View.GameScene;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class GameSceneRefreshThread {
    private final GameController gameController;
    private final Timer refreshTimer;
    private final int REFRESH_INTERVAL = 4000;
    private GameScene gameScene;
    private int count;

    public GameSceneRefreshThread(GameController gameController, GameScene gameScene) {
        this.gameController = gameController;
        this.refreshTimer = new Timer();
        this.gameScene = gameScene;
        this.count = 0;
    }

    public void start() {
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0, REFRESH_INTERVAL);
    }

    public void stop() {
        refreshTimer.cancel();
    }

    private void refresh() {
        if(gameController.getTurnPlayerId() != gameController.getCurrentPlayer().getIdPlayer()) {
            Platform.runLater(() -> {
                if(gameController.getGame().getGameHasEnded()) {
                    gameScene.stopRefreshThread();
                    gameController.showEndGameScene();
                }
                else {
                    gameController.refreshGame();
                    gameScene.updateLayout();
                }
            });
        }
    }
}
