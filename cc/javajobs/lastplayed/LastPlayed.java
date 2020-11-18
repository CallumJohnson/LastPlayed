package cc.javajobs.lastplayed;

/*
    
    Created By:     JavaJobs
    Created In:     Nov/2020
    Project Name:   LastPlayed
    Package Name:   cc.javajobs.lastplayed
    Class Purpose:  The main class of the project.
    
*/

import cc.javajobs.api.API;
import cc.javajobs.api.JavaJobsAPI;
import cc.javajobs.lastplayed.commands.LastPlayedCommand;
import cc.javajobs.lastplayed.events.Listeners;
import cc.javajobs.lastplayed.logic.PlayTimeManager;
import cc.javajobs.lastplayed.logic.TimeFrameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LastPlayed extends JavaPlugin {

    // Declared Fields.
    private static LastPlayed instance;
    public HashMap<String, List<UUID>> cache;
    private API api;

    /**
     * Required Methodology provided by SpigotAPI for loading the Plugin.
     */
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance=this;
        this.api = JavaJobsAPI.hook(this);

        // This Project Requires 'Essentials' to be Loaded.
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            api.console().log("Found Essentials, Hooking and Pulling Data now!");
            new PlayTimeManager();
            new TimeFrameManager();
        }

        // Register the Listener Manager for the plugin.
        api.registerListener(new Listeners());

        // Register the Central Command handler for the plugin.
        api.registerCommand(LastPlayedCommand.class);

        long diff = System.currentTimeMillis()-start;

        // Output Message, with author etc. as project was created for Free.
        api.console().log("LastPlayed initialised in " + diff + " ms!");
        api.console().warn("&aPlugin Developed By&7: &cC A L L U M#4160 &8(&bDiscord&8)");
    }

    /**
     * Method to return a static instance of the Plugin's Main class.
     *
     * @return - LastPlayed Object.
     */
    public static LastPlayed get() {
        return instance;
    }

    /**
     * Method to return an instance of the API Hook.
     *
     * @return - API Object.
     */
    public API getAPI() {
        return api;
    }
}
