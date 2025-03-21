package dev.hypinohaizin;

import dev.hypinohaizin.commands.Ban;
import dev.hypinohaizin.commands.Kick;
import dev.hypinohaizin.commands.TempBan;
import dev.hypinohaizin.commands.Unban;
import dev.hypinohaizin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnniPunishments extends JavaPlugin {
   public void onEnable() {
      this.loadConfig();
      Bukkit.getPluginManager().registerEvents(new JoinLeaveEvent(), this);
      this.getCommand("kick").setExecutor(new Kick());
      this.getCommand("tempban").setExecutor(new TempBan());
      this.getCommand("ban").setExecutor(new Ban());
      this.getCommand("unban").setExecutor(new Unban());
      getLogger().info("[AnniPunishments] Plugin Loaded!...");
   }

   public void loadConfig() {
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
   }
}
