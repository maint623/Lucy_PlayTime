package kr.lucymc.lucy_playtime;

import kr.lucymc.lucy_playtime.Lucy_PlayTime.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

import static kr.lucymc.lucy_playtime.Lucy_PlayTime.PlayTimes;
import static kr.lucymc.lucy_playtime.PlayTime_DB.*;

public class PlayTime_Event implements Listener {
    private BukkitRunnable task;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String player = event.getPlayer().getUniqueId().toString();
        Player players = event.getPlayer();
        String tableName = "playtime";
        String columnName = "UserID";
        String value = ""+players.getUniqueId();
        boolean dataExists = isDataExists(tableName, columnName, value);
        if(!dataExists){
            PlayInsert(players.getUniqueId(),0);
            PlayTimes.put(""+players.getUniqueId(),0);
        }else{
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Lucy_PlayTime.getInstance(),new Runnable() {public void run() {
                PlayTimes.put(""+players.getUniqueId(),PlaySelect(players.getUniqueId()));
                task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!PlayTimes.containsKey("" + event.getPlayer().getUniqueId())) {
                            task.cancel();
                        } else {
                            PlayTimes.put(player, PlayTimes.get(player) + 1);
                        }
                    }
                };
                task.runTaskTimer(Lucy_PlayTime.getInstance(),0L, 20L);
            }}, 3);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayUpdate(player.getUniqueId(),PlayTimes.get(""+player.getUniqueId()));
        PlayTimes.remove(""+player.getUniqueId());
    }
}

