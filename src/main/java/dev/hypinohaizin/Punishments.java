package dev.hypinohaizin;

import dev.hypinohaizin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Punishments extends JavaPlugin {
   public void onEnable() {
      this.loadConfig();
      System.out.println("[AnniPunishments] Plugin Loaded...");
      Bukkit.getPluginManager().registerEvents(new JoinLeaveEvent(), this);
      Bukkit.getPluginManager().registerEvents(new PlayerChat(), this);
      this.getCommand("mute").setExecutor(new Mute());
      this.getCommand("unmute").setExecutor(new Unmute());
      this.getCommand("kick").setExecutor(new Kick());
      this.getCommand("tempban").setExecutor(new TempBan());
      this.getCommand("ban").setExecutor(new Ban());
      this.getCommand("unban").setExecutor(new Unban());
   }

   public void loadConfig() {
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
   }
}
