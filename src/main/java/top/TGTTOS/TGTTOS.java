package top.TGTTOS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import top.TGTTOS.listeners.GameListener;
import top.TGTTOS.listeners.PlayerListener;
import top.TGTTOS.room.RoomManager;

import java.io.File;

public class TGTTOS extends JavaPlugin {
    private static TGTTOS instance;

    public static TGTTOS getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File resourceFolder = new File(dataFolder, "resource");
        if (!resourceFolder.exists()) resourceFolder.mkdirs();

        File dataFile = new File(resourceFolder, "data.yml");

        GameConfig.init(dataFile);
        RoomManager.getInstance();

        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("§aTGTTOS 插件已成功启动！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("tgttos")) {
            if (args.length == 0) {
                sender.sendMessage("§c用法: /tgttos join | leave | set");
                return true;
            }

            if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof org.bukkit.entity.Player)) {
                    sender.sendMessage("§c只有玩家能使用！");
                    return true;
                }
                org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
                RoomManager.getInstance().getRoom(1).addPlayer(p);
                return true;
            }

            if (args[0].equalsIgnoreCase("leave")) {
                if (!(sender instanceof org.bukkit.entity.Player)) {
                    sender.sendMessage("§c只有玩家能使用！");
                    return true;
                }
                org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
                for (top.TGTTOS.room.Room room : RoomManager.getInstance().getRooms()) {
                    if (room.hasPlayer(p)) {
                        room.removePlayer(p);
                        sender.sendMessage("§a你已离开房间");
                        return true;
                    }
                }
                sender.sendMessage("§c你不在任何房间里");
                return true;
            }

            if (args[0].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage("§c用法: /tgttos set <回合> <spawn1/spawn2/spawn3/finish>");
                    return true;
                }
                if (!(sender instanceof org.bukkit.entity.Player)) return true;
                org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
                try {
                    int round = Integer.parseInt(args[1]);
                    String type = args[2];
                    boolean success = GameConfig.get().savePoint(round, type, p.getLocation());
                    if (success) {
                        p.sendMessage("§a成功保存点位！");
                    } else {
                        p.sendMessage("§c保存失败！");
                    }
                } catch (Exception e) {
                    p.sendMessage("§c格式错误！");
                }
                return true;
            }
        }
        return false;
    }
}
