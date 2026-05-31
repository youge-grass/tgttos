package top.TGTTOS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameConfig {
    private static GameConfig instance;
    private final File dataFile;
    public final Random random = new Random();

    public Location lobbyLocation;
    public int minPlayers = 2;
    public int maxPlayers = 16;
    public int countdownSeconds = 30;

    public final int TOTAL_MAPS = 10;
    public final int TOTAL_ROUND_PER_GAME = 6;
    public final int TOTAL_EVENT = 6;

    public static class RoundMap {
        public Location spawn1;
        public Location spawn2;
        public Location spawn3;
        public Location finish;
    }

    public final Map<Integer, RoundMap> roundMapPool = new HashMap<>();

    private GameConfig(File file) {
        this.dataFile = file;
        reloadAllConfig();
    }

    public static void init(File dataYml) {
        if (instance == null) {
            instance = new GameConfig(dataYml);
        }
    }

    public static GameConfig get() {
        return instance;
    }

    public void reloadAllConfig() {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        lobbyLocation = loadSingleLoc(cfg, "lobby");

        minPlayers = cfg.getInt("game.min-players", 2);
        maxPlayers = cfg.getInt("game.max-players", 16);
        countdownSeconds = cfg.getInt("game.countdown", 30);

        roundMapPool.clear();
        for (int i = 1; i <= TOTAL_MAPS; i++) {
            RoundMap map = new RoundMap();
            String pre = "rounds." + i + ".";
            map.spawn1 = loadSingleLoc(cfg, pre + "spawn1");
            map.spawn2 = loadSingleLoc(cfg, pre + "spawn2");
            map.spawn3 = loadSingleLoc(cfg, pre + "spawn3");
            map.finish = loadSingleLoc(cfg, pre + "finish");
            roundMapPool.put(i, map);
        }
    }

    public boolean savePoint(int roundId, String posKey, Location loc) {
        if (roundId < 1 || roundId > TOTAL_MAPS) return false;
        if (!posKey.equals("spawn1") && !posKey.equals("spawn2")
                && !posKey.equals("spawn3") && !posKey.equals("finish")) return false;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        String path = "rounds." + roundId + "." + posKey;
        saveSingleLoc(cfg, path, loc);
        try {
            cfg.save(dataFile);
            reloadAllConfig();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RoundMap getRandomMap() {
        int r = random.nextInt(TOTAL_MAPS) + 1;
        return roundMapPool.get(r);
    }

    public int randomEvent() {
        if (random.nextDouble() <= 0.25) {
            return random.nextInt(TOTAL_EVENT) + 1;
        }
        return 0;
    }

    private Location loadSingleLoc(YamlConfiguration cfg, String path) {
        String worldName = cfg.getString(path + ".world");
        if (worldName == null || worldName.isBlank()) return null;
        World w = Bukkit.getWorld(worldName);
        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        return new Location(w, x, y, z);
    }

    private void saveSingleLoc(YamlConfiguration cfg, String path, Location loc) {
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
    }
}
