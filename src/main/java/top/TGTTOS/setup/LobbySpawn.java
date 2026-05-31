package top.TGTTOS.setup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import top.TGTTOS.TGTTOS;

import java.io.File;
import java.io.IOException;

public class LobbySpawn implements Listener {
    private final TGTTOS plugin;
    private final File dataFile;

    public LobbySpawn(TGTTOS main) {
        this.plugin = main;
        File resDir = new File(plugin.getDataFolder(), "resource");
        if (!resDir.exists()) resDir.mkdirs();
        this.dataFile = new File(resDir, "data.yml");

        try {
            if (!dataFile.exists()) dataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Location getLobbyLoc() {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        String worldName = cfg.getString("lobby.world");
        if (worldName == null || worldName.isEmpty()) return null;

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;

        double x = cfg.getDouble("lobby.x");
        double y = cfg.getDouble("lobby.y");
        double z = cfg.getDouble("lobby.z");

        return new Location(world, x, y, z);
    }

    @EventHandler
    public void onSetLobby(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (hand.getType() != Material.EMERALD_BLOCK) return;
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
            p.sendMessage("§a大厅出生点已保存！");
        } catch (IOException ex) {
            p.sendMessage("§c保存失败！");
            ex.printStackTrace();
        }
    }
}