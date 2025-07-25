package me.hypinohaizin.util;

import me.hypinohaizin.NekolPunishments;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Webhook {

    public static void sendRaw(String webhookUrl, String content) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }

            connection.getInputStream().close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEnabledOnly(String content) {
        FileConfiguration config = NekolPunishments.getInstance().getConfig();
        if (!config.getBoolean("webhook-enabled"))
            return;

        String webhookUrl = config.getString("webhook");
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            sendRaw(webhookUrl, content);
        }
    }
}
