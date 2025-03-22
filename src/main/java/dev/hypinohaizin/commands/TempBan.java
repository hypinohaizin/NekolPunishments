package dev.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.hypinohaizin.NekolPunishments;
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
               File playerfile = new File(NekolPunishments.getPlugin(NekolPunishments.class).getDataFolder() + File.separator, "punishments.yml");
               FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerfile);
               String uuid = null;
               if (target != null) {
                   uuid = target.getPlayer().getUniqueId().toString();
               }
            if (uuid == null) {
                for (String key : playerData.getKeys(false)) {
                    if (playerData.getString(key + ".name").equalsIgnoreCase(args[0])) {
                        uuid = key;
                    }
                }
            }

            if (uuid == null) {
               sender.sendMessage("§cそのプレイヤーは存在しません");
               return false;
            }

            long unixTime = System.currentTimeMillis() / 1000L;
            long banTime = parsePeriod(args[1]) / 1000L - 1L;
            if (banTime < 59L) {
               sender.sendMessage("§c1分未満のBANは設定できません。");
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
                        sender.sendMessage("§c[NekolPunishments]" + Bukkit.getPlayer(args[0]).getName() + "§6 §cが§e" + args[1] + " §c日間BANされました 理由:§b" + reason);
                        target.kickPlayer("§6Banned!\n" +"理由: " + reason + "\n" +
                                "§c BAN終了までの期間: §e" + calculateTime((long) playerData.getInt((uuid) + ".ban.length") - unixTime));
                        Bukkit.broadcastMessage("§c§l[NekoLMC]" + target.getDisplayName() + "さんは、" + reason + "のため" + args[1] + "日間BANされました");
                     } else {
                        sender.sendMessage("§c[NekolPunishments]" + args[0] + "§6 §cが§e" + args[1] + " §cBANされました 理由:§b" + reason);
                        //sender.sendMessage("§cBANNED §6" + args[0] + " §cfor §e" + args[1] + " §cfor §b" + reason);

                     }


                  } catch (IOException ignored) {
                  }
               } else {
                  sender.sendMessage("§cそのプレイヤーはすでにBANされています。");
               }
            }
         } else {
            sender.sendMessage("§c無効なコマンド構文: /tempban <名前> <期間> <理由>");
         }
      } else {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n" + "§c十分な権限があるのに実行できない場合は開発者に報告してください。");
      }

      return false;
   }

   public static String calculateTime(long seconds) {
      int days = (int)TimeUnit.SECONDS.toDays(seconds);
      long hours = TimeUnit.SECONDS.toHours(seconds) - (long)(days * 24);
      long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
      long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
       return (" " + days + "日間 " + hours + "時間 " + minute + "分間 " + second + "秒間").replace(" 0日間", "").replace(" 0時間", "").replace(" 0分間", "").replace(" 0秒間", "").replaceFirst(" ", "");
   }

   public static Long parsePeriod(String period) {
      if (period == null) {
         return null;
      } else {
         period = period.toLowerCase(Locale.JAPANESE);
         Matcher matcher = periodPattern.matcher(period);
         Instant instant = Instant.EPOCH;

         while(matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch(typ.hashCode()) {
               case 100:
                  if (typ.equals("分間")) {
                     instant = instant.plus(Duration.ofDays(num));
                  }
                  break;
               case 104:
                  if (typ.equals("時間")) {
                     instant = instant.plus(Duration.ofHours(num));
                  }
                  break;
               case 109:
                  if (typ.equals("分間")) {
                     instant = instant.plus(Duration.ofMinutes(num));
                  }
            }
         }

         return instant.toEpochMilli();
      }
   }
}