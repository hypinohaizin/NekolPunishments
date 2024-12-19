package dev.hypinohaizin.events;

import java.io.File;
import java.io.IOException;

import dev.hypinohaizin.AnniPunishments;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Ban
        implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender.hasPermission("punishments.ban")) {
         if (args.length >= 2) {
            String reason = "";
            int i = 1;
            while (i < args.length) {
               reason = String.valueOf(reason) + args[i] + " ";
               ++i;
            }
            reason = reason.substring(0, reason.length() - 1);
            Player target = Bukkit.getPlayerExact(args[0]);
            File playerfile = new File(((AnniPunishments)AnniPunishments.getPlugin(AnniPunishments.class)).getDataFolder() + File.separator, "punishments.yml");
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
            String uuid = null;
            if (target != null) {
               uuid = target.getPlayer().getUniqueId().toString();
            }
            if (uuid == null) {
               for (String key : playerData.getKeys(false)) {
                  if (!playerData.getString(String.valueOf(key) + ".name").equalsIgnoreCase(args[0])) continue;
                  uuid = key;
               }
            }
            if (uuid == null) {
               sender.sendMessage("§cPlayer does not exist.");
               return false;
            }
            if (playerData.contains(uuid)) {
               if (!playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                     String pwd = RandomStringUtils.random(8, characters);
                     playerData.set(uuid + ".ban.isbanned", true);
                     playerData.set(uuid + ".ban.reason", reason);
                     playerData.set(uuid + ".ban.length", -1);
                     playerData.set(uuid + ".ban.id", pwd);
                     playerData.save(playerfile);
                     if (target != null) {
                        sender.sendMessage("§cBANNED §6" + Bukkit.getPlayer(args[0]).getName() + " §cfor §e" + reason);
                        target.kickPlayer("§c§lBANNED\n" +
                                "§6" + reason +"\n" +
                                "§cThis ban is permanent\n" +
                                "§aAppeal ID: §e" + playerData.getString(playerData.getString(uuid + ".ban.id" + "\nCreate a ticket in Discord (Fast Support)")));
                     } else {
                        sender.sendMessage("§cBANNED §6" + args[0] + " §cfor §e" + reason);
                     }

                  } catch (IOException var12) {
                     var12.printStackTrace();
                  }
               } else {
                  sender.sendMessage("§cPlayer is already banned!");
               }
            }
         } else {
            sender.sendMessage("§cInvalid syntax. Correct: /ban <name> <reason>");
         }
      } else {
         sender.sendMessage("§cYou do not have permission to execute this command!");
      }

      return false;
   }
}
