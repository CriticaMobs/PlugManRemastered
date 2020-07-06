package com.rylinaux.plugman.dropped;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class JsonButtonRegisterEvent
        implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer ();
        JsonButton button = null;
        for(JsonButton jsonButton : JsonButton.values ())
        {
            if(!e.getMessage ().equalsIgnoreCase (jsonButton.getUUID (null)))
            { continue; }
            button = jsonButton;
            break;
        }
        if(button == null) return;
        e.setMessage ("");
        e.setCancelled (true);
        if(button.runnable () == null) return;
        button.runnable ().run ();
        JsonButton.CANCEL.setRunnable (null);
        JsonButton.LOAD.setRunnable (null);
    }

    @EventHandler
    public void onConsole(ServerCommandEvent e)
    {
        String result = e.getCommand ().toLowerCase ();
        if(JsonButton.LOAD.runnable () == null && JsonButton.CANCEL.runnable () == null) return;
        e.setCancelled (true);
        switch (result)
        {
            case "да": JsonButton.LOAD.runnable ().run (); break;
            case "нет": JsonButton.CANCEL.runnable ().run (); break;
        }
        JsonButton.CANCEL.setRunnable (null);
        JsonButton.LOAD.setRunnable (null);
    }
}
