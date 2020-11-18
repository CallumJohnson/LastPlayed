package cc.javajobs.lastplayed.commands;

/*
    
    Created By:     JavaJobs
    Created In:     Nov/2020
    Project Name:   LastPlayed
    Package Name:   cc.javajobs.lastplayed.commands
    Class Purpose:  An implementation of my CustomCommand API.
    
*/

import cc.javajobs.api.generic.command.CustomCommand;
import cc.javajobs.api.generic.command.SubCommand;
import cc.javajobs.api.generic.message.Placeholders;
import cc.javajobs.lastplayed.LastPlayed;
import cc.javajobs.lastplayed.logic.PlayTimeManager;
import cc.javajobs.lastplayed.logic.TimeFrameManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

public class LastPlayedCommand extends CustomCommand<LastPlayed> {

    /**
     * A extended Constructor which takes the variable of a Plugin using Reflection.
     * Is not used in ways such as (new LastPlayedCommand(Plugin plugin)).
     *
     * @param plugin - Plugin instance, handled by Reflection.
     */
    public LastPlayedCommand(LastPlayed plugin) {
        super(
                plugin,
                "lastplayed",
                "§cSorry, you don't have the correct permissions to do that!",
                "lastplayed.command"
        );
    }

    /**
     * A Method to return different data based on where the user presses TAB to Auto-Complete the command.
     *
     * @param commandSender - TAB Presser.
     * @param strings - Arguments at that current moment.
     * @return - List<\String> based on the Arguments.
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            return Collections.singletonList("query");
        }
        return Collections.emptyList();
    }

    /**
     * A default method to handle any unrecognised commands.
     *
     * @param commandSender - Sender who sent the command to this method.
     * @param strings - Arguments provided with the command.
     * @return - True = Command Completed, False = Command Failed.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, String[] strings) {
        return help(commandSender, strings);
    }

    /**
     * Method to send a text-wall to the sender, all lines are command-specific and provide help to the user.
     *
     * @param sender - Sender who sent the command to this method.
     * @param args - Arguments provided with the command.
     * @return - True = Command Completed, False = Command Failed.
     */
    @SubCommand(name = "help", description = "Help-Page for all things 'LastPlayed'.", usage = "/LastPlayed Help", perm = "null")
    public boolean help(CommandSender sender, String[] args) {
        String line = "&c{USAGE}&7: &f{DESC}";
        for (String entry : this.subCommandMap.keySet()) {
            String usage = getCommandUsage(entry);
            Map<String, String> data = new HashMap<>();
            data.put("USAGE", usage);
            data.put("DESC", getCommandDescription(entry));
            sender.sendMessage(Placeholders.replaceStrPlaceholders(translate(line), data));
        }
        return true;
    }

    /**
     * Method to Query the LastPlayed database.
     *
     * @param sender - Sender who sent the command to this method.
     * @param args - Arguments provided with the command.
     * @return - True = Command Completed, False = Command Failed.
     */
    @SubCommand(
            name = "query",
            description = "Query the Pulled Data.",
            usage = "/LastPlayed Query (count)(timeframe) <page>",
            perm = "lastplayed.query"
    )
    public boolean query(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            String input = args[0];
            String numeric = input.replaceAll("[a-zA-Z]*", "");
            String timeFrame = input.replaceFirst("[0-9]*", "");
            if (!TimeFrameManager.get().recognisedTimeFrame(timeFrame)) {
                sender.sendMessage("§cPlease refer to the manual!");
                return help(sender, args);
            }
            int queryTime;
            try {
                queryTime = Integer.parseInt(numeric);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cPlease enter a valid numeric!");
                return false;
            }
            long distance = TimeFrameManager.get().getDistance(queryTime, timeFrame);
            if (distance == -1) {
                sender.sendMessage("§cAn error occurred in the calculation of the timeframe you provided.");
                return false;
            }
            long query = System.currentTimeMillis() - distance;
            List<UUID> players = PlayTimeManager.get().get(query);
            if (players.size() > 10) {
                sender.sendMessage("§cThis query is multi-paged as there is so many entries, " +
                        "showing you the first page!");
            }
            List<UUID> cache = new ArrayList<>();
            int index;
            try {
                index = args.length >= 2 ? 10 + (Integer.parseInt(args[1]) * 10) : 10;
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cAn error occurred in the calculation of the timeframe you provided.");
                return false;
            }
            sender.sendMessage(translate("&cShowing Entries&7: &f" + index + "&7/&f" + players.size()));
            for (int i = 0; i < index; i++) {
                try {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(players.get(i));
                    sender.sendMessage("#" + (i+1) + ": " + player.getName());
                    cache.add(players.get(i));
                } catch (IndexOutOfBoundsException ex) {
                    break;
                }
            }

            LastPlayed.get().cache.put(sender.getName(), cache);
            sender.sendMessage(translate("&bTo remove these players from the whitelist. " +
                    "Do &8'&f/LastPlayed whitelistremove&8'&b!"));
            return true;
        } else {
            sender.sendMessage("§cPlease enter the correct amount of arguments!");
            return help(sender, args);
        }
    }

    /**
     * Method to display Examples of the Query Command to the user.
     *
     * @param sender - Sender who sent the command to this method.
     * @param args - Arguments provided with the command.
     * @return - True = Command Completed, False = Command Failed.
     */
    @SubCommand(
            name = "examples",
            description = "Display Valid Examples to use for your own Functionality.",
            usage = "/LastPlayed examples",
            perm = "lastplayed.examples"
    )
    public boolean examples(CommandSender sender, String[] args) {
        String[] examples = new String[] {
                "&c/LastPlayed Query 1d &7- &f1 Day",
                "&c/LastPlayed Query 2d &7- &f2 Days",
                "&c/LastPlayed Query 3w &7- &f3 Weeks",
                "&c/LastPlayed Query 5w &7- &f5 Weeks",
                "&c/LastPlayed Query 4m &7- &f4 Months",
                "&c/LastPlayed Query 1y &7- &f1 Year"
        };
        Arrays.stream(examples).map(this::translate).forEach(sender::sendMessage);
        return true;
    }

    /**
     * Method to remove Cached results from the Server Whitelist.
     *
     * @param sender - Sender who sent the command to this method.
     * @param args - Arguments provided with the command.
     * @return - True = Command Completed, False = Command Failed.
     */
    @SubCommand(
            name = "WhitelistRemove",
            description = "Remove Cached results from the Server Whitelist.",
            usage = "/LastPlayed whitelistRemove",
            perm = "lastplayed.whitelistRemove"
    )
    public boolean whitelistRemove(CommandSender sender, String[] args) {
        if (!LastPlayed.get().cache.containsKey(sender.getName())) {
            sender.sendMessage("§cPlease run a query before running this command!");
            return false;
        } else {
            for (UUID uuid : LastPlayed.get().cache.get(sender.getName())) {
                Bukkit.getOfflinePlayer(uuid).setWhitelisted(false);
            }
            Bukkit.reloadWhitelist();
            sender.sendMessage(translate("&cThose players have been removed from the whitelist."));
            return true;
        }
    }

}
