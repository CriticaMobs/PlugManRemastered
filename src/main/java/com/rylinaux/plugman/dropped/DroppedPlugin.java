package com.rylinaux.plugman.dropped;

import com.rylinaux.plugman.PlugMan;
import com.rylinaux.plugman.util.PluginUtil;
import com.rylinaux.plugman.util.ThreadUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class DroppedPlugin extends BukkitRunnable {

    Map<String, Long> initializedPlugins = new HashMap<> ();

    private boolean isLoading;
    private int timer = 5;

    @Override
    public void run() {
        if(timer != 0)
        {
            timer--;
        }else if(!isLoading){
            setLoading (true);
        }
        for(File file : getFiles ())
        {
            if(!file.getName ().endsWith (".jar")) continue;

            String key = getKey (file);

            if(key == null){
                initializedPlugin (file);
                continue;
            }
            if(initializedPlugins.get (key) == file.length ()) continue;

            initializedPlugins.put (key, file.length ());

            if(PluginUtil.isIgnored (key)) continue;

            sendJson (key, "load.json");
        }
    }

    public void sendJson(String key, String path)
    {
        String jsonStart = PlugMan.getInstance ().getMessageFormatter ().prefix (get (path + ".start", key));
        String jsonLoad = get (path + ".load", key);
        String loadTool = get (path + ".load-tooltip", key);
        String or = get (path + ".or", key);
        String jsonCancel = get (path + ".cancel", key);
        String cancelTool = get (path + ".cancel-tooltip", key);
        String yes = get ("reload.json-yes", key);
        String no = get ("reload.json-no", key);
        TextComponent component = new TextComponent (jsonStart);
        TextComponent loadClick = new TextComponent (jsonLoad);
        loadClick.setHoverEvent (new HoverEvent (HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText (loadTool)));
        loadClick.setClickEvent (new ClickEvent (ClickEvent.Action.RUN_COMMAND, JsonButton.LOAD.getUUID (() -> {
            PluginUtil.loadPlugin (key);
            Bukkit.broadcast (yes, "plugman.admin");
        })));
        component.addExtra (loadClick);
        component.addExtra (or);
        TextComponent cancel = new TextComponent (jsonCancel);
        cancel.setHoverEvent (new HoverEvent (HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText (cancelTool)));
        cancel.setClickEvent (new ClickEvent (ClickEvent.Action.RUN_COMMAND, JsonButton.CANCEL.getUUID (() -> {
            Bukkit.broadcast (no, "plugman.admin");
        })));
        component.addExtra (cancel);
        PlugMan.getInstance ().getMessageFormatter ().sendOp (component);
        Bukkit.getLogger ().info (component.getText () + jsonLoad + or + jsonCancel);
    }

    public String get(String path, Object... objects)
    {
        return PlugMan.getInstance ().getMessageFormatter ().format (false, path, objects);
    }

    public List<File> getFiles()
    {
        List<File> fileList = new ArrayList<> ();
        File parent = PlugMan.getInstance ().getDataFolder ().getParentFile ();
        if(parent.listFiles () != null) fileList = Arrays.asList (Objects.requireNonNull (parent.listFiles ()));
        return fileList;
    }

    public void initializedPlugin(File file)
    {
        String name;
        try {
            name = PlugMan.getInstance().getPluginLoader().getPluginDescription(file).getName ();
            if(isLoading && !PluginUtil.isIgnored (name)) {
                sendJson (name, "dropped.json");
            }else{
                Bukkit.getLogger ().info (PlugMan.getInstance ().getMessageFormatter ().format ("load.adapted", name));
            }
            initializedPlugins.put (name, file.length ());
        } catch (InvalidDescriptionException e) {
            e.printStackTrace ();
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getKey(File f)
    {
        String s = null;
        try {
            PluginDescriptionFile desc = PlugMan.getInstance().getPluginLoader().getPluginDescription(f);
            if(initializedPlugins.containsKey (desc.getName ()))
            {
                s = desc.getName ();
            }
        } catch (InvalidDescriptionException e) {
            e.printStackTrace ();
        }
        return s;
    }

}
