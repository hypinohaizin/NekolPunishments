package dev.hypinohaizin.events;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import dev.hypinohaizin.AnniPunishments;
import org.apache.commons.lang.RandomStringUtils;
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
               sender.sendMessage("§cPlayer does not exist.");
               return false;
            }

            if (playerData.contains(uuid)) {
               if (!playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                     String pwd = RandomStringUtils.random(8, characters);
                     playerData.set(uuid + ".ban.isbanned", true);
                     playerData.set(uuid + ".ban.reason", reason);
                     playerData.set(uuid + ".ban.length", -1);
                     playerData.set(uuid + ".ban.id", pwd);
                     playerData.save(playerfile);
                     if (target == null) {
                        sender.sendMessage("§cPermanently banned §e" + args[0] + " §cfor §b" + reason);
                     }

                     if (target != null) {
                        sender.sendMessage("§cPermanently banned §e" + Bukkit.getPlayer(args[0]).getName() + " §cfor §b" + reason);
                        target.getPlayer().kickPlayer("§cYou are permanently banned from this server!\n\n" +
                                "§cReason: §b" + playerData.getString(uuid + ".ban.reason") + "\n" +
                                "§7Find out more: §b§n" + ((AnniPunishments) AnniPunishments.getPlugin(AnniPunishments.class)).getConfig().getString("bandomain") + "\n\n" +
                                "§cBan ID: §e#" + playerData.getString(uuid + ".ban.id") + "\n" +
                                "§eSharing your Ban ID may affect the processing of your appeal!");
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
