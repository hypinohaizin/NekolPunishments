package me.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import me.hypinohaizin.NekolPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {
   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nekolpunishments.ban")) {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n§c十分な権限があるのに実行できない場合はサーバーオーナーに報告してください。");
         return true;
      }
      if (args.length < 2) {
         sender.sendMessage("§c無効なコマンド構文: /ban <名前> <理由>");
         return true;
      }

      String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
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
         sender.sendMessage("§cそのプレイヤーはすでにBANされています。");
         return true;
      }

      try {
         playerData.set(uuid + ".ban.isbanned", true);
         playerData.set(uuid + ".ban.reason", reason);
         playerData.set(uuid + ".ban.length", -1);
         playerData.save(playerFile);

         if (target != null) {
            sender.sendMessage("§c[NekolPunishments] " + target.getName() + " さんが理由: " + reason + " のため永久BANされました");
            target.kickPlayer("§6Banned! \n理由: " + reason);
            Bukkit.broadcastMessage("§c§l[NekoLサーバー] " + target.getDisplayName() + " さんが理由: " + reason + " のため永久BANされました");
         } else {
            sender.sendMessage("§c[NekolPunishments] " + args[0] + " さんが理由: " + reason + " のため永久BANされました");
         }
      } catch (IOException e) {
         sender.sendMessage("§cBANの実行中にエラーが発生しました。");
      }

      return true;
   }
}
