package me.hypinohaizin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nekolpunishments.kick")) {
            sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n§c十分な権限があるのに実行できない場合はサーバーオーナーに報告してください。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c無効なコマンド構文: /kick <名前> <理由>");
            return true;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]);
            if (i < args.length - 1) reasonBuilder.append(" ");
        }
        String reason = reasonBuilder.toString();

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cそのプレイヤーは存在しないかオフラインです。");
            return true;
        }

        sender.sendMessage("§a[NekolPunishments] " + target.getName() + " が理由: " + reason + " のためKickされました。");
        target.kickPlayer("§6Kicked!\n§6理由: §f" + reason);
        return true;
    }
}
