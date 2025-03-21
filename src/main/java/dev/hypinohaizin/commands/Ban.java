package dev.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import dev.hypinohaizin.AnniPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Ban implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         if (sender.hasPermission("punishments.ban")) {
            if (args.length >= 2) {
               String reason = "";
               for(int i = 1; i < args.length; ++i) {
                  reason = reason + args[i] + " ";
               }
               reason = reason.substring(0, reason.length() - 1);
               Player target = Bukkit.getPlayerExact(args[0]);
               File playerfile = new File(AnniPunishments.getPlugin(AnniPunishments.class).getDataFolder() + File.separator, "punishments.yml");
               FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
               String uuid = null;
               if (target != null) {
                  uuid = target.getPlayer().getUniqueId().toString();
               }

            String characters;
            if (uuid == null) {
                for (String s : playerData.getKeys(false)) {
                    characters = s;
                    if (playerData.getString(characters + ".name").equalsIgnoreCase(args[0])) {
                        uuid = characters;
                    }
                }
            }

            if (uuid == null) {
               sender.sendMessage("§cそのプレイヤーは存在しません。");
               return false;
            }

            if (playerData.contains(uuid)) {
               if (!playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     playerData.set(uuid + ".ban.isbanned", true);
                     playerData.set(uuid + ".ban.reason", reason);
                     playerData.set(uuid + ".ban.length", -1);
                     playerData.save(playerfile);
                     if (target != null) {
                        sender.sendMessage("§c" + Bukkit.getPlayer(args[0]).getName() + "さんが 理由: " + reason + "のため永久BANされました");
                        target.kickPlayer("§6Banned! 理由: "+ reason);
                        Bukkit.broadcastMessage("§c§l" + target.getDisplayName() + "さんが理由:" + reason +"のため永久BANされました");
                     } else {
                        sender.sendMessage("§cBanned §6" + args[0] + "さんが 理由: " + reason + "のため永久BANされました");
                     }

                  } catch (IOException var12) {
                  }
               } else {
                  sender.sendMessage("§cそのプレイヤーはすでにBANされています。");
               }
            }
         } else {
            sender.sendMessage("§c無効なコマンド構文: /ban <名前> <理由>");
         }
      } else {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n" + "§c十分な権限があるのに実行できない場合はDevに報告してください。");
      }

      return false;
   }
}
