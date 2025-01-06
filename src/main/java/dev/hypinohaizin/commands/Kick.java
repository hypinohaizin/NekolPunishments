package dev.hypinohaizin.commands;

import dev.hypinohaizin.AnniPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender.hasPermission("punishments.kick")) {
         if (args.length >= 2) {
            String reason = "";

            for(int i = 1; i < args.length; ++i) {
               reason = reason + args[i] + " ";
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               sender.sendMessage("§cそのプレイヤーは存在しないか存在しません。");
               return false;
            }

            sender.sendMessage("§a" + Bukkit.getPlayer(args[0]).getName() + "が 理由:" + reason + "のためKickされました。");
            target.kickPlayer("§6You have been kicked!\n\n§7Reason: §f" + reason);
         } else {
            sender.sendMessage("§c無効なコマンド構文: /kick <name> <reason>");
         }
      } else {
         sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n" + "§c十分な権限があるのに実行できない場合はDevに報告してください。");
      }

      return false;
   }
}
