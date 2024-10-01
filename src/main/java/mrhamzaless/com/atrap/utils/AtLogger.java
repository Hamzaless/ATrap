package mrhamzaless.com.atrap.utils;

import org.bukkit.Bukkit;

public class AtLogger {

    public static String log(String string) {
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color(string));
        return string;
    }


}
