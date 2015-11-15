package net.downwithdestruction.effects;

import net.downwithdestruction.effects.commands.DarkCloud;
import net.downwithdestruction.effects.commands.TestCommand;
import net.downwithdestruction.effects.commands.TestCommandTwo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by madmac on 9/16/15.
 */
public class Effects extends JavaPlugin {
    public static Effects instance;
    private PluginManager pm = Bukkit.getPluginManager();

    public Effects() {
        instance = this;
    }

    public static Effects getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        registerCommands();
    }


    @Override
    public void onDisable() {

    }

    public void registerCommands(){
        this.getCommand("darkcloud").setExecutor(new DarkCloud(this));
        this.getCommand("testcommand").setExecutor(new TestCommand(this));
        this.getCommand("testcommandtwo").setExecutor(new TestCommandTwo(this));

    }

}
