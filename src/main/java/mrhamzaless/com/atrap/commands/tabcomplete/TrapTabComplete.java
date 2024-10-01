package mrhamzaless.com.atrap.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrapTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return suggestions;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("add", "buy", "admin", "reload", "rename", "invite", "accept", "deny", "settings", "leave"));
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                suggestions.addAll(Arrays.asList("chunks", "new"));
            }
            if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("chunks")) {
                suggestions.add("add");
            }
            else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("reject")) {
                for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                    suggestions.add(onlinePlayer.getName());
                }
            }
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("chunks") && args[2].equalsIgnoreCase("add")) {
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                suggestions.add(onlinePlayer.getName());
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        List<String> result = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(lastArg)) {
                result.add(suggestion);
            }
        }

        return result;
    }
}
