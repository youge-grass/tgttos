package top.TGTTOS.setup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import top.TGTTOS.TGTTOS;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSpawn implements Listener {
    private final TGTTOS plugin;
    private final File dataFile;
    private final YamlConfiguration dataConfig;
    private final Map<Player, Integer> stepMap = new HashMap<>();

    public GameSpawn(TGTTOS plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "resource/data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSetGameSpawn(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (!e.getAction().toString().contains("RIGHT")) return;
        if (item == null || item.getType() != Material.IRON_BLOCK) return;
        if (!p.isOp()) return;

        e.setCancelled(true);
        int step = stepMap.getOrDefault(p, 1);
        Location loc = p.getLocation();

        switch (step) {
            case 1:
                dataConfig.set("spawns.spawn1.world", loc.getWorld().getName());
                dataConfig.set("spawns.spawn1.x", loc.getX());
                dataConfig.set("spawns.spawn1.y", loc.getY());
                dataConfig.set("spawns.spawn1.z", loc.getZ());
                p.sendMessage("§a已设置 游戏出生点 1");
                break;
            case 2:
                dataConfig.set("spawns.spawn2.world", loc.getWorld().getName());
                dataConfig.set("spawns.spawn2.x", loc.getX());
                dataConfig.set("spawns.spawn2.y", loc.getY());
                dataConfig.set("spawns.spawn2.z", loc.getZ());
                p.sendMessage("§a已设置 游戏出生点 2");
                break;
            case 3:
                dataConfig.set("spawns.spawn3.world", loc.getWorld().getName());
                dataConfig.set("spawns.spawn3.x", loc.getX());
                dataConfig.set("spawns.spawn3.y", loc.getY());
                dataConfig.set("spawns.spawn3.z", loc.getZ());
                p.sendMessage("§a已设置 游戏出生点 3");
                break;
        }

        stepMap.put(p, step >= 3 ? 1 : step + 1);
        save();
    }

    private void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}