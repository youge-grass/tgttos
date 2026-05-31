package top.TGTTOS.listeners;

import top.TGTTOS.GameConfig;

public class GameManager {

    private static GameManager instance;

    public static GameManager get() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    // 随机地图（调用 GameConfig）
    public GameConfig.RoundMap nextRandomMap() {
        return GameConfig.get().getRandomMap();
    }

    // 随机事件
    public int nextRandomEvent() {
        return GameConfig.get().randomEvent();
    }
}