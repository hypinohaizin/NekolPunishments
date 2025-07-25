package me.hypinohaizin.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([dhm])");

    public static String calculateTime(long seconds) {
        int days = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        long second = seconds - TimeUnit.SECONDS.toMinutes(seconds) * 60L;

        String result = (" " + days + "d " + hours + "h " + minute + "m " + second + "s")
                .replace(" 0d", "")
                .replace(" 0h", "")
                .replace(" 0m", "")
                .replace(" 0s", "")
                .replaceFirst(" ", "");
        return result.isEmpty() ? "0s" : result;
    }

    public static Long parsePeriod(String period) {
        if (period == null) return null;
        period = period.toLowerCase(Locale.JAPANESE);
        Matcher matcher = PERIOD_PATTERN.matcher(period);
        Instant instant = Instant.EPOCH;
        boolean found = false;
        while (matcher.find()) {
            found = true;
            int num = Integer.parseInt(matcher.group(1));
            switch (matcher.group(2)) {
                case "d" -> instant = instant.plus(Duration.ofDays(num));
                case "h" -> instant = instant.plus(Duration.ofHours(num));
                case "m" -> instant = instant.plus(Duration.ofMinutes(num));
            }
        }
        return found ? instant.toEpochMilli() : null;
    }
}
