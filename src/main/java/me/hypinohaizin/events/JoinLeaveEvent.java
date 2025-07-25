package me.hypinohaizin.events;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.hypinohaizin.NekolPunishments;
import me.hypinohaizin.util.Timer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveEvent implements Listener {
   @EventHandler
   public void onLeave(PlayerQuitEvent event) {
      String uuid = event.getPlayer().getUniqueId().toString();
      File playerFile = new File(NekolPunishments.getInstance().getDataFolder(), "punishments.yml");
      FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
      if (playerData.contains(uuid) && playerData.getBoolean(uuid + ".ban.isbanned")) {
         event.setQuitMessage(null);
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      File playerFile = new File(NekolPunishments.getInstance().getDataFolder(), "punishments.yml");
      FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
      String uuid = event.getPlayer().getUniqueId().toString();
      long unixTime = System.currentTimeMillis() / 1000L;

      if (playerData.contains(uuid) && playerData.getBoolean(uuid + ".ban.isbanned")) {
         long banLength = playerData.getLong(uuid + ".ban.length");
         if (banLength <= unixTime && banLength != -1) {
            try {
               playerData.set(uuid + ".ban.isbanned", false);
               playerData.set(uuid + ".ban.reason", "");
               playerData.set(uuid + ".ban.length", 0);
               playerData.save(playerFile);
            } catch (IOException ignored) {}
         }

         event.setJoinMessage(null);

         if (playerData.getLong(uuid + ".ban.length") == -1) {
            event.getPlayer().kickPlayer("§6Banned! \n理由: " + playerData.getString(uuid + ".ban.reason"));
         } else {
            if (playerData.getLong(uuid + ".ban.length") == 0) {
               return;
            }
            event.getPlayer().kickPlayer(
                    "§6Banned!\n理由: " + playerData.getString(uuid + ".ban.reason") +
                            "\n§c BAN終了までの期間: §e" + Timer.calculateTime(playerData.getLong(uuid + ".ban.length") - unixTime)
            );
         }
      }

      if (!playerData.contains(uuid)) {
         try {
            playerData.createSection(uuid);
            playerData.set(uuid + ".name", event.getPlayer().getName());
            playerData.createSection(uuid + ".ban");
            playerData.set(uuid + ".ban.isbanned", false);
            playerData.set(uuid + ".ban.reason", "");
            playerData.set(uuid + ".ban.length", 0);
            playerData.save(playerFile);
         } catch (IOException ignored) {}
      }
   }
}
