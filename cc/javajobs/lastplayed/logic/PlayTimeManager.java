package cc.javajobs.lastplayed.logic;

/*
    
    Created By:     JavaJobs
    Created In:     Nov/2020
    Project Name:   LastPlayed
    Package Name:   cc.javajobs.lastplayed.logic
    Class Purpose:  A manager to Load, query and store the data from the Essentials Database.
    
*/

import cc.javajobs.api.API;
import cc.javajobs.lastplayed.LastPlayed;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PlayTimeManager {

    private static PlayTimeManager instance;
    private final HashMap<UUID, Long> lastPlayed = new HashMap<>();;
    private final HashMap<Exception, Integer> errorMap = new HashMap<>();

    public PlayTimeManager() {
        instance = this;
        init(); // Initialise the plugin's database.
    }

    /**
     * Method to return an instance of PlayTimeManager to make the project more portable and quick to modify.
     *
     * @return - PlayTimeManager Object.
     */
    public static PlayTimeManager get() {
        return instance;
    }

    /**
     * Method to load Essentials data into this plugin through their file system.
     */
    private void init() {
        API api = LastPlayed.get().getAPI();
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (ess == null) {
            api.console().error("Essentials hasn't been found. " +
                    "This plugin requires Essentials UserData and the Essentials Plugin to be loaded.");
            return;
        }
        File file = ess.getDataFolder();
        if (!file.exists()) {
            api.console().error("Data cannot be Pulled as the Essentials Data folder couldn't be found!");
            return;
        }
        File userDataFolder = new File(file, "userdata/");
        if (!userDataFolder.exists()) {
            api.console().error("Data cannot be Pulled as the Essentials UserData folder couldn't be found!");
            return;
        }
        File[] files = userDataFolder.listFiles();
        api.console().log("Found " + files.length + " files to handle.");

        for (File userFile : files) {
            try {
                UUID uuid = UUID.fromString((userFile.getName()).replaceFirst("[.][^.]+$", ""));
                YamlConfiguration config = YamlConfiguration.loadConfiguration(userFile);
                long logout = config.getLong("timestamps.logout");
                lastPlayed.put(uuid, logout);
            } catch (Exception ex) {
                errorMap.put(ex, errorMap.containsKey(ex) ? errorMap.get(ex) + 1 : 1);
            }
        }

        api.console().log("Out of " + files.length + " files, " + lastPlayed.size() + " entries have been loaded.");

        if (files.length != lastPlayed.size()) {
            api.console().warn("Some files have been lost due to exceptions.");
            for (Map.Entry<Exception, Integer> entry : errorMap.entrySet()) {
                api.console().warn("");
                api.console().error("Exception: " + entry.getKey().getClass().getSimpleName());
                api.console().error("Count: " + entry.getValue());
                api.console().warn("");
            }
        }

        api.console().log("Finished pulling data.");
    }

    /**
     * Method to update a User's logout time on the fly, useful for Long-Running servers.
     *
     * @param uuid - UUID for the Time to be Updated for.
     */
    public void updateLogoutTime(UUID uuid) {
        this.lastPlayed.put(uuid, System.currentTimeMillis());
        LastPlayed.get().getAPI().console().log("Updated '" + uuid.toString() + "' to reflect true data.");
    }

    /**
     * Method to get all entries in the database based on the given user-input.
     *
     * @param targetDate - The target date which users will eb queries against.
     * @return - List<\UUID> of users who match the criteria.
     */
    public List<UUID> get(long targetDate) {
        return lastPlayed.entrySet()
                .stream()
                .filter(entry -> entry.getValue() <= targetDate)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
