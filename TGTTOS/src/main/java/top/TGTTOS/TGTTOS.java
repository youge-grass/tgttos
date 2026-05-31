package top.TGTTOS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import top.TGTTOS.listeners.GameListener;
import top.TGTTOS.listeners.PlayerListener;
import top.TGTTOS.room.RoomManager;

import java.io.File;

public class TGTTOS extends JavaPlugin {
    private static TGTTOS main;

    public static TGTTOS getInstance() {
        return main;
    }

    @Override
    public void onEnable() {
        main = this;

        //创建配置文件夹
        File data = getDataFolder();
        if(!data.exists()) data.mkdirs();
        File res = new File(data,"resource");
        if(!res.exists()) res.mkdirs();
        File dataYml = new File(res,"data.yml");

        //初始化配置
        GameConfig.init(dataYml);
        RoomManager.getInstance();

        //注册监听器（统一传this）
        getServer().getPluginManager().registerEvents(new GameListener(this),this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);

        getLogger().info("TGTTOS加载完成");
    }

    //【核心：重写父类onCommand，最简指令，删掉CommandListener，所有指令写这里】
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("tgttos")){
            //在这里写/tgttos逻辑，原CommandListener代码全部挪进这里
            if(args.length == 0){
                sender.sendMessage("用法：/tgttos join/create");
                return true;
            }
            //示例判断
            if(args[0].equalsIgnoreCase("join")){
                //进房间逻辑
            }else if(args[0].equalsIgnoreCase("create")){
                //创建房间逻辑
            }
            return true;
        }
        return false;
    }
}