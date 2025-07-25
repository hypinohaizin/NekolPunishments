package me.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.hypinohaizin.NekolPunishments;
import me.hypinohaizin.util.Webhook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class UnbanCommand implements CommandExecutor, TabCompleter {
   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nekolpunishments.unban")) {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n§c十分な権限があるのに実行できない場合はサーバーオーナーに報告してください。");
         return true;
      }
      if (args.length < 1) {
         sender.sendMessage("§c無効なコマンド構文: /unban <name>");
         return true;
      }

      Player target = Bukkit.getPlayerExact(args[0]);
      File playerFile = new File(NekolPunishments.getInstance().getDataFolder(), "punishments.yml");
      FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
      String uuid = null;

      if (target != null) {
         uuid = target.getUniqueId().toString();
      }

      if (uuid == null) {
         for (String key : playerData.getKeys(false)) {
            if (playerData.getString(key + ".name", "").equalsIgnoreCase(args[0])) {
               uuid = key;
               break;
            }
         }
      }

      if (uuid == null) {
         sender.sendMessage("§cそのプレイヤーは存在しません。");
         return true;
      }

      if (!playerData.contains(uuid)) {
         sender.sendMessage("§cそのプレイヤーのデータが存在しません。");
         return true;
      }

      if (playerData.getBoolean(uuid + ".ban.isbanned", false)) {
         try {
            playerData.set(uuid + ".ban.isbanned", false);
            playerData.set(uuid + ".ban.reason", "");
            playerData.set(uuid + ".ban.length", 0);
            playerData.set(uuid + ".ban.id", "");
            playerData.save(playerFile);

            Webhook.sendEnabledOnly(":white_check_mark: **UNBAN** - `" + args[0] + "` のBANが解除されました。");

            if (target != null) {
               sender.sendMessage("§a[NekolPunishments] " + target.getName() + " のBANが解除されました。");
            } else {
               sender.sendMessage("§a[NekolPunishments] " + args[0] + " のBANが解除されました。");
            }
         } catch (IOException e) {
            sender.sendMessage("§cBANデータの保存中にエラーが発生しました。");
         }
      } else {
         sender.sendMessage("§cそのプレイヤーはBANされてません。");
      }
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> suggestions = new ArrayList<>();
      if (args.length == 1) {
         File playerFile = new File(NekolPunishments.getInstance().getDataFolder(), "punishments.yml");
         FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
         for (String key : playerData.getKeys(false)) {
            if (playerData.getBoolean(key + ".ban.isbanned", false)) {
               String name = playerData.getString(key + ".name", null);
               if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                  suggestions.add(name);
               }
            }
         }
      }
      return suggestions;
   }
}
