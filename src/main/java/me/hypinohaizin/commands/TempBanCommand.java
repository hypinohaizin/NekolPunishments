package me.hypinohaizin.commands;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hypinohaizin.NekolPunishments;
import me.hypinohaizin.util.Timer;
import me.hypinohaizin.util.Webhook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TempBanCommand implements CommandExecutor {
    private static final Pattern periodPattern = Pattern.compile("([0-9]+)([dhm])");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nekolpunishments.tempban")) {
            sender.sendMessage("§cあなたはこのコマンドを実行する権限がありません。\n§c十分な権限があるのに実行できない場合はサーバーオーナーに報告してください。");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("§c無効なコマンド構文: /tempban <名前> <期間> <理由>");
            return true;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; ++i) {
            reasonBuilder.append(args[i]);
            if (i < args.length - 1) reasonBuilder.append(" ");
        }
        String reason = reasonBuilder.toString();

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
            sender.sendMessage("§cそのプレイヤーは存在しません");
            return true;
        }

        long unixTime = System.currentTimeMillis() / 1000L;
        Long banPeriodMillis = Timer.parsePeriod(args[1]);
        if (banPeriodMillis == null || banPeriodMillis / 1000L < 59L) {
            sender.sendMessage("§c1分未満のBANは設定できません。");
            return true;
        }
        long banTime = banPeriodMillis / 1000L - 1L;

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
            playerData.set(uuid + ".ban.length", unixTime + banTime);
            playerData.save(playerFile);

            Webhook.sendEnabledOnly(":hourglass_flowing_sand: **TEMPBAN** - `" + args[0] + "` が " + args[1] + " 間BANされました。理由: " + reason);

            if (target != null) {
                sender.sendMessage("§c[NekolPunishments]" + target.getName() + " §6§cが§e" + args[1] + " §c間BANされました 理由:§b" + reason);
                target.kickPlayer("§6Banned!\n理由: " + reason + "\n§cBAN終了までの期間: §e" + Timer.calculateTime(playerData.getLong(uuid + ".ban.length") - unixTime));
                Bukkit.broadcastMessage("§c§l[NekoLサーバー]" + target.getDisplayName() + "さんは、" + reason + "のため" + args[1] + "間BANされました");
            } else {
                sender.sendMessage("§c[NekolPunishments]" + args[0] + " §6§cが§e" + args[1] + " §cBANされました 理由:§b" + reason);
            }
        } catch (IOException e) {
            sender.sendMessage("§cBANの実行中にエラーが発生しました。");
        }

        return true;
    }
}
