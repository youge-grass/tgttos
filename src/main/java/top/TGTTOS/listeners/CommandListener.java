package top.TGTTOS.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.TGTTOS.GameConfig;
import top.TGTTOS.room.Room;
import top.TGTTOS.room.RoomManager;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage("§c仅玩家可执行指令");
            return true;
        }

        //========= OP点位设置 /tgttos set round1 spawn1 =========
        if(args.length>=3 && args[0].equalsIgnoreCase("set")){
            if(!p.isOp()){
                p.sendMessage("§c无管理员权限");
                return true;
            }
            String roundStr = args[1];
            String posKey = args[2];
            int rid;
            try {
                rid = Integer.parseInt(roundStr.replace("round",""));
            }catch (Exception e){
                p.sendMessage("§c格式错误：/tgttos set round1 spawn1");
                return true;
            }
            //GameConfig已有savePoint，不再报错
            boolean res = GameConfig.get().savePoint(rid,posKey,p.getLocation());
            if(res){
                p.sendMessage("§a成功保存 round"+rid+"-"+posKey);
            }else {
                p.sendMessage("§c保存失败：回合1~10，点位spawn1/spawn2/spawn3/finish");
            }
            return true;
        }

        //========= join进房间：RoomManager.getInstance() =========
        if(args.length==2 && args[0].equalsIgnoreCase("join")){
            try {
                int roomId = Integer.parseInt(args[1]);
                //原.get() → getInstance()
                Room targetRoom = RoomManager.getInstance().getRoom(roomId);
                if(targetRoom==null){
                    p.sendMessage("§c仅开放1/2/3房间");
                    return true;
                }
                targetRoom.addPlayer(p);
            }catch (NumberFormatException e){
                p.sendMessage("§c用法：/tgttos join 1");
            }
            return true;
        }

        //========= leave离开房间：RoomManager.getInstance() =========
        if(args.length==1 && args[0].equalsIgnoreCase("leave")){
            boolean out = false;
            //原.get() → getInstance()
            for(Room room : RoomManager.getInstance().getRooms()){
                if(room.hasPlayer(p)){
                    room.removePlayer(p);
                    out = true;
                    break;
                }
            }
            p.sendMessage(out?"§a成功离开房间":"§c你不在任何房间");
            return true;
        }

        p.sendMessage("§e可用：/tgttos join 1 | /tgttos leave | OP:/tgttos set round1 spawn1");
        return true;
    }
}