package dev.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.hypinohaizin.AnniPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TempBan implements CommandExecutor {
   private static final Pattern periodPattern = Pattern.compile("([0-9]+)([hdwmy])");

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender.hasPermission("punishments.tempban")) {
         if (args.length >= 3) {
            String reason = "";

            for(int i = 2; i < args.length; ++i) {
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

            if (uuid == null) {
               Iterator var11 = playerData.getKeys(false).iterator();

               while(var11.hasNext()) {
                  String key = (String)var11.next();
                  if (playerData.getString(key + ".name").equalsIgnoreCase(args[0])) {
                     uuid = key;
                  }
               }
            }

            if (uuid == null) {
               sender.sendMessage("§cPlayer does not exist.");
               return false;
            }

            long unixTime = System.currentTimeMillis() / 1000L;
            long banTime = parsePeriod(args[1]) / 1000L - 1L;
            if (banTime < 59L) {
               sender.sendMessage("§cYou can not ban someone for less than 1 minute.");
               return false;
            }

            if (playerData.contains(uuid)) {
               if (!playerData.getBoolean(uuid + ".ban.isbanned")) {
                  try {
                     playerData.set(uuid + ".ban.isbanned", true);
                     playerData.set(uuid + ".ban.reason", reason);
                     playerData.set(uuid + ".ban.length", unixTime + banTime);
                     playerData.save(playerfile);
                     if (target != null) {
                        sender.sendMessage("§cBANNED §6" + Bukkit.getPlayer(args[0]).getName() + " §cfor §e" + args[1] + " §cfor §b" + reason);
                        target.kickPlayer("§6Banned!\n" +
                                "§c expire in: §e" + calculateTime((long) playerData.getInt((uuid) + ".ban.length") - unixTime));
                     } else {
                        sender.sendMessage("§cBANNED §6" + args[0] + " §cfor §e" + args[1] + " §cfor §b" + reason);
                     }


                  } catch (IOException var16) {
                     var16.printStackTrace();
                  }
               } else {
                  sender.sendMessage("§cPlayer is already banned!");
               }
            }
         } else {
            sender.sendMessage("§cInvalid syntax. Correct: /tempban <name> <length> <reason>");
         }
      } else {
         sender.sendMessage("§cYou do not have permission to execute this command!");
      }

      return false;
   }

   public static String calculateTime(long seconds) {
      int days = (int)TimeUnit.SECONDS.toDays(seconds);
      long hours = TimeUnit.SECONDS.toHours(seconds) - (long)(days * 24);
      long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
      long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
      String time = (" " + days + "d " + hours + "h " + minute + "m " + second + "s").toString().replace(" 0d", "").replace(" 0h", "").replace(" 0m", "").replace(" 0s", "").replaceFirst(" ", "");
      return time;
   }

   public static Long parsePeriod(String period) {
      if (period == null) {
         return null;
      } else {
         period = period.toLowerCase(Locale.ENGLISH);
         Matcher matcher = periodPattern.matcher(period);
         Instant instant = Instant.EPOCH;

         while(matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch(typ.hashCode()) {
               case 100:
                  if (typ.equals("d")) {
                     instant = instant.plus(Duration.ofDays((long)num));
                  }
                  break;
               case 104:
                  if (typ.equals("h")) {
                     instant = instant.plus(Duration.ofHours((long)num));
                  }
                  break;
               case 109:
                  if (typ.equals("m")) {
                     instant = instant.plus(Duration.ofMinutes((long)num));
                  }
            }
         }

         return instant.toEpochMilli();
      }
   }
}