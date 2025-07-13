package me.hypinohaizin;

import me.hypinohaizin.commands.*;
import me.hypinohaizin.events.JoinLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NekolPunishments extends JavaPlugin {

   private static NekolPunishments INSTANCE;

   @Override
   public void onEnable() {
      INSTANCE = this;
      saveDefaultConfig();
      getLogger().info("[NekolPunishments] enabled!");

      // コマンド
      getCommand("ban").setExecutor(new BanCommand());
      getCommand("kick").setExecutor(new KickCommand());
      getCommand("tempban").setExecutor(new TempBanCommand());
      getCommand("unban").setExecutor(new UnbanCommand());
      getCommand("unban").setTabCompleter(new UnbanCommand());
      // イベント
      getServer().getPluginManager().registerEvents(new JoinLeaveEvent(), this);
   }

   @Override
   public void onDisable() {
      getLogger().info("[NekolPunishments] disabled!");
   }

   public static NekolPunishments getInstance() {
      return INSTANCE;
   }
}
