package top.TGTTOS.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import top.TGTTOS.TGTTOS;

import java.io.File;
import java.io.IOException;

public class PlayerListener implements Listener {
    private final TGTTOS plugin;
    private final File dataFile;

    public PlayerListener(TGTTOS plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "resource/data.yml");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!dataFile.exists()) return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        String worldName = cfg.getString("lobby.world");
        if (worldName == null) return;

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return;

        double x = cfg.getDouble("lobby.x");
        double y = cfg.getDouble("lobby.y");
        double z = cfg.getDouble("lobby.z");

        Location lobby = new Location(world, x, y, z);
        p.teleport(lobby);
        p.sendMessage("§a已传送到大厅!");
    }

    @EventHandler
    public void onRightClickSetLobby(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item.getType() != Material.EMERALD_BLOCK) return;
        if (!p.isOp()) return;

        e.setCancelled(true);
        Location loc = e.getClickedBlock().getLocation();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);

        cfg.set("lobby.world", loc.getWorld().getName());
        cfg.set("lobby.x", loc.getX());
        cfg.set("lobby.y", loc.getY());
        cfg.set("lobby.z", loc.getZ());

        try {
            cfg.save(dataFile);
            p.sendMessage("§a大厅出生点设置成功!");
        } catch (IOException ex) {
            p.sendMessage("§c设置失败!");
            ex.printStackTrace();
        }
    }
}