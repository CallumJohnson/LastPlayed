package cc.javajobs.lastplayed.events;

/*
    
    Created By:     JavaJobs
    Created In:     Nov/2020
    Project Name:   LastPlayed
    Package Name:   cc.javajobs.lastplayed.events
    Class Purpose:  Event Management, listens to a variety of events with varying purposes.
    
*/

import cc.javajobs.lastplayed.logic.PlayTimeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    /**
     * EventHandling method to update logout time upon quit.
     *
     * @param event - PlayerQuitEvent, a SpigotAPI Event.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        try {
            PlayTimeManager.get().updateLogoutTime(event.getPlayer().getUniqueId());
        } catch (NullPointerException ignored) {}
    }

    /**
     * EventHandling method to update logout time upon quit.
     *
     * @param event - PlayerKickEvent, a SpigotAPI Event.
     */
    @EventHandler
    public void onKick(PlayerKickEvent event) {
        try {
            PlayTimeManager.get().updateLogoutTime(event.getPlayer().getUniqueId());
        } catch (NullPointerException ignored) {}
    }

}
