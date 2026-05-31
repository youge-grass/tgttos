package top.TGTTOS.room;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.TGTTOS.GameConfig;
import top.TGTTOS.TGTTOS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private int roomState = 0; //0等待 1倒计时 2对局
    private final int roomId;
    private final List<Player> playerList = new ArrayList<>();
    private BukkitTask countTask;
    private int nowRound = 0;
    private final GameConfig cfg = GameConfig.get();
    private final Random random = new Random();
    private int nowEventId = 0;

    public Room(int id) {
        this.roomId = id;
    }

    public boolean addPlayer(Player p) {
        if (playerList.contains(p)) {
            p.sendMessage("§c你已在房间" + roomId);
            return false;
        }
        if (playerList.size() >= cfg.maxPlayers) {
            p.sendMessage("§c房间已满");
            return false;
        }
        playerList.add(p);
        if (cfg.lobbyLocation != null) p.teleport(cfg.lobbyLocation);
        p.sendMessage("§a加入房间" + roomId + " | " + playerList.size() + "/" + cfg.maxPlayers);
        checkStartCount();
        return true;
    }

    public void removePlayer(Player p) {
        playerList.remove(p);
        p.sendMessage("§c退出房间" + roomId);
        if (playerList.isEmpty()) resetRoom();
        if (roomState == 1 && playerList.size() < cfg.minPlayers) {
            stopCount();
            roomState = 0;
            sendAllMsg("§c人数不足，倒计时取消");
        }
    }

    public boolean hasPlayer(Player p) {
        return playerList.contains(p);
    }

    private void checkStartCount() {
        if (roomState != 0 || countTask != null) return;
        if (playerList.size() >= cfg.minPlayers) startCountDown();
    }

    private void startCountDown() {
        roomState = 1;
        int time = cfg.countdownSeconds;
        countTask = new BukkitRunnable() {
            int left = time;

            @Override
            public void run() {
                playerList.removeIf(pl -> !pl.isOnline());
                if (playerList.size() < cfg.minPlayers) {
                    cancel();
                    countTask = null;
                    roomState = 0;
                    sendAllMsg("§c人数不足，倒计时终止");
                    return;
                }
                if (left <= 0) {
                    cancel();
                    countTask = null;
                    startNewRound();
                    return;
                }
                sendAllTitle("§e" + left, "秒后开局");
                left--;
            }
        }.runTaskTimer(TGTTOS.getInstance(), 0, 20);
    }

    private void startNewRound() {
        roomState = 2;
        nowRound++;
        GameConfig.RoundMap map = cfg.getRandomMap();
        nowEventId = cfg.getRandomEvent();
        if (nowEventId > 0) sendAllMsg("§e本回合触发随机事件" + nowEventId + "(待实现)");
        sendAllMsg("§a第" + nowRound + "回合开始");

        for (Player p : playerList) {
            Location spawn = switch (random.nextInt(3)) {
                case 0 -> map.spawn1;
                case 1 -> map.spawn2;
                default -> map.spawn3;
            };
            if (spawn != null) p.teleport(spawn);
        }
    }

    public void nextRound() {
        if (nowRound >= cfg.TOTAL_ROUND_PER_GAME) {
            sendAllMsg("§a6回合全部结束，房间重置");
            resetRoom();
            return;
        }
        startNewRound();
    }

    public void resetRoom() {
        stopCount();
        roomState = 0;
        nowRound = 0;
        nowEventId = 0;
        playerList.clear();
    }

    private void stopCount() {
        if (countTask != null) countTask.cancel();
        countTask = null;
    }

    public void sendAllMsg(String msg) {
        playerList.forEach(p -> p.sendMessage(msg));
    }

    // 删掉Title类，沿用原版sendTitle（消除Cannot resolve symbol 'Title'报错）
    public void sendAllTitle(String main, String sub) {
        playerList.forEach(p -> p.sendTitle(main, sub, 0, 25, 5));
    }

    // getter
    public int getId() {
        return roomId;
    }

    public List<Player> getRoomPlayers() {
        return playerList;
    }

    public int getRoomStatus() {
        return roomState;
    }
}