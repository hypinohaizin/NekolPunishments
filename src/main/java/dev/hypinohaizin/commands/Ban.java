
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
            Player target = Bukkit.getPlayerExact(args[0]);
            File playerfile = new File(((AnniPunishments) AnniPunishments.getPlugin(AnniPunishments.class)).getDataFolder() + File.separator, "punishments.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
            String uuid = null;
            if (target != null) {
               uuid = target.getPlayer().getUniqueId().toString();
            }

            String characters;
            if (uuid == null) {
               Iterator var11 = playerData.getKeys(false).iterator();

               while(var11.hasNext()) {
                  characters = (String)var11.next();
                  if (playerData.getString(characters + ".name").equalsIgnoreCase(args[0])) {
                     uuid = characters;
                  }
               }
            }

            if (uuid == null) {
               sender.sendMessage("§cそのプレイヤーは存在しません");
               return false;
            }

            if (playerData.contains(uuid)) {
               if (!playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     playerData.set(uuid + ".ban.isbanned", true);
                     playerData.set(uuid + ".ban.length", -1);
                     playerData.save(playerfile);
                     if (target != null) {
                        sender.sendMessage("§cBanned §6" + Bukkit.getPlayer(args[0]).getName());
                        target.kickPlayer("§6Banned!");
                     } else {
                        sender.sendMessage("§cBanned §6" + args[0]);
                     }

                  } catch (IOException var12) {
                     var12.printStackTrace();
                  }
               } else {
                  sender.sendMessage("§cそのプレイヤーはすでにBANされています。");
               }
            }
         } else {
            sender.sendMessage("§c無効なコマンド構文: /ban <name>");
         }
      } else {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n" + "§c十分な権限があるのに実行できない場合はDevに報告してください。");
      }

      return false;
   }
}
