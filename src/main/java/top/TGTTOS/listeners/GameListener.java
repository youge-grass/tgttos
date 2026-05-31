package top.TGTTOS.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import top.TGTTOS.GameConfig;
import top.TGTTOS.TGTTOS;
import top.TGTTOS.room.Room;
import top.TGTTOS.room.RoomManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameListener implements Listener {
    private final TGTTOS plugin;
    //通关冷却：玩家UUID-结束时间戳，3秒CD防反复通关
    private final Map<UUID, Long> finishCool = new HashMap<>();
    private static final long CD_MS = 3000;

    public GameListener(TGTTOS main) {
        this.plugin = main;
    }

    //玩家进服传送大厅
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Location lobby = GameConfig.get().lobbyLocation;
        if(lobby != null) p.teleport(lobby);
    }

    //踩终点通关跳回合【核心逻辑】
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        //小优化：原地转头不触发判定，减少性能损耗
        if(e.getFrom().getBlockX() == e.getTo().getBlockX()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ()
                && e.getFrom().getBlockY() == e.getTo().getBlockY()) return;

        Player p = e.getPlayer();
        UUID uid = p.getUniqueId();
        long now = System.currentTimeMillis();

        //冷却拦截
        if(finishCool.containsKey(uid) && finishCool.get(uid) > now) return;

        List<Room> allRooms = RoomManager.getInstance().getRooms();
        for(Room room : allRooms) {
            //不在房间内 或 非游戏进行中(state=2)直接跳过
            if(!room.hasPlayer(p) || room.getState() != 2) continue;

            GameConfig.RoundMap roundMap = GameConfig.get().getRandomMap();
            //没有配置终点直接返回
            if(roundMap == null || roundMap.finish == null) return;
            Location finishLoc = roundMap.finish;
            Location playerLoc = p.getLocation();

            //终点范围判定
            boolean reachFinish = playerLoc.getWorld().equals(finishLoc.getWorld())
                    && Math.abs(playerLoc.getX() - finishLoc.getX()) <= 1
                    && Math.abs(playerLoc.getZ() - finishLoc.getZ()) <= 1
                    && Math.abs(playerLoc.getY() - finishLoc.getY()) <= 1.5;

            if(reachFinish) {
                finishCool.put(uid, now + CD_MS);
                p.sendMessage("§a成功抵达终点，本回合通关！");
                room.nextRound();
                break; //找到所属房间，跳出循环
            }
        }
    }

    //等待房间（state=0）禁止破坏/放置/交互/PVP，OP豁免
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if(p.isOp()) return;
        for(Room r : RoomManager.getInstance().getRooms()){
            if(r.hasPlayer(p) && r.getState() == 0){
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(p.isOp()) return;
        for(Room r : RoomManager.getInstance().getRooms()){
            if(r.hasPlayer(p) && r.getState() == 0){
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(p.isOp()) return;
        for(Room r : RoomManager.getInstance().getRooms()){
            if(r.hasPlayer(p) && r.getState() == 0){
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player p)) return;
        if(p.isOp()) return;
        for(Room r : RoomManager.getInstance().getRooms()){
            if(r.hasPlayer(p) && r.getState() == 0){
                e.setCancelled(true);
                return;
            }
        }
    }
}