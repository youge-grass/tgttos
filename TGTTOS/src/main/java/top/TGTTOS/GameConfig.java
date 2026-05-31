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

    //大厅坐标
    public Location lobbyLocation;
    //游戏全局参数
    public int minPlayers = 2;
    public int maxPlayers = 16;
    public int countdownSeconds = 30;

    //固定配置
    public final int TOTAL_MAPS = 10;
    public final int TOTAL_ROUND_PER_GAME = 6;
    public final int TOTAL_EVENT = 6;

    //单回合地图结构：3出生+终点
    public static class RoundMap{
        public Location spawn1;
        public Location spawn2;
        public Location spawn3;
        public Location finish;
    }
    //存储1~10号回合地图
    public final Map<Integer,RoundMap> roundMapPool = new HashMap<>();

    private GameConfig(File file){
        this.dataFile = file;
        reloadAllConfig();
    }

    //初始化单例
    public static GameConfig init(File dataYml){
        if(instance == null) instance = new GameConfig(dataYml);
        return instance;
    }
    public static GameConfig get(){
        return instance;
    }

    //从yml加载全部配置
    public void reloadAllConfig(){
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        //加载大厅
        lobbyLocation = loadSingleLoc(cfg,"lobby");
        //加载游戏参数
        minPlayers = cfg.getInt("game.min-players",2);
        maxPlayers = cfg.getInt("game.max-players",16);
        countdownSeconds = cfg.getInt("game.countdown",30);
        //加载10个回合点位
        roundMapPool.clear();
        for(int i=1;i<=TOTAL_MAPS;i++){
            RoundMap map = new RoundMap();
            String pre = "rounds."+i+".";
            map.spawn1 = loadSingleLoc(cfg,pre+"spawn1");
            map.spawn2 = loadSingleLoc(cfg,pre+"spawn2");
            map.spawn3 = loadSingleLoc(cfg,pre+"spawn3");
            map.finish = loadSingleLoc(cfg,pre+"finish");
            roundMapPool.put(i,map);
        }
    }

    //【补缺失的savePoint方法：给/tgttos set指令调用】
    public boolean savePoint(int roundId,String posKey,Location loc){
        if(roundId<1 || roundId>TOTAL_MAPS) return false;
        if(!posKey.equals("spawn1")&&!posKey.equals("spawn2")&&!posKey.equals("spawn3")&&!posKey.equals("finish")) return false;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        String path = "rounds."+roundId+"."+posKey;
        saveSingleLoc(cfg,path,loc);
        try {
            cfg.save(dataFile);
            reloadAllConfig();//保存后重载
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //随机抽取一张地图
    public RoundMap getRandomMap(){
        int ran = random.nextInt(TOTAL_MAPS)+1;
        return roundMapPool.get(ran);
    }

    //随机事件 0=无事件 1~6事件
    public int getRandomEvent(){
        if(random.nextDouble()<=0.25){
            return random.nextInt(TOTAL_EVENT)+1;
        }
        return 0;
    }

    //坐标序列化/反序列化工具
    private Location loadSingleLoc(YamlConfiguration cfg,String path){
        String worldName = cfg.getString(path+".world");
        if(worldName==null||worldName.isBlank()) return null;
        World w = Bukkit.getWorld(worldName);
        double x = cfg.getDouble(path+".x");
        double y = cfg.getDouble(path+".y");
        double z = cfg.getDouble(path+".z");
        return new Location(w,x,y,z);
    }
    private void saveSingleLoc(YamlConfiguration cfg,String path,Location loc){
        cfg.set(path+".world",loc.getWorld().getName());
        cfg.set(path+".x",loc.getX());
        cfg.set(path+".y",loc.getY());
        cfg.set(path+".z",loc.getZ());
    }
}