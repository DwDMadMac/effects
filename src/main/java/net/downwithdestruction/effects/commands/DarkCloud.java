package net.downwithdestruction.effects.commands;

import net.downwithdestruction.effects.Effects;
import net.downwithdestruction.effects.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/**
 * Created by madmac on 9/16/15.
 */
public class DarkCloud implements CommandExecutor {
    private Effects plugin;
    private boolean isRunning;
    private boolean isRunningTime;
    private int cloudTimer;
    private BukkitTask taskToCancel;
    private BukkitTask taskCoolDownToCancel;
    private HashMap<Player, Integer> coolDownTime = new HashMap<Player, Integer>();
    private String DwD = ChatColor.DARK_RED + "[" + ChatColor.GRAY + "DwD" + ChatColor.DARK_RED + "] ";

    public DarkCloud(Effects plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Only Players may user this command!");
            return true;
        }

        if(!commandSender.hasPermission("effects.command.darkcloud")){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "You do not have permission to use DarkCloud!");
            return true;
        }

        if(args.length < 1){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Please enter a time length of 1 - 60 seconds."); //command.getDescription()  <--- This will get command description from plugin .yml
            return true;
        }

        try{
            cloudTimer = Integer.valueOf(args[0]);
        }catch (NumberFormatException numberFormatException){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Please enter a time length of 1 - 60!");
            return true;
        }

        if(cloudTimer < 0 || cloudTimer > 60){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Please enter a time length of 1 - 60!");
            return true;
        }

        final Player target;

        if(args.length < 2){
            target = (Player) commandSender;
        }else {
            target = Bukkit.getPlayer(args[1]);

            if(target == null){
                commandSender.sendMessage(DwD + ChatColor.GOLD + "Could not find player!");
                return true;
            }

            if(!commandSender.hasPermission("effects.command.darkcloud.others")){
                commandSender.sendMessage(DwD + ChatColor.GREEN + "" + target + " does not have permission to use this command on other players.");
                return  true;
            }

            if(target.hasPermission("effects.exempt.darkcloud")){

                if(coolDownTime.containsKey(target)){
                    commandSender.sendMessage(DwD + ChatColor.GOLD + "You can not put a Dark Cloud on " + target.getName() +  " for another " + coolDownTime.get(target) + " seconds!");
                    return true;
                }

                if(isRunning){
                    commandSender.sendMessage(DwD + ChatColor.GOLD + target.getName() + " can only have one Dark Cloud running at a time!");
                    return true;
                }

                commandSender.sendMessage(DwD + ChatColor.GREEN + "You put a dark cloud on " + target.getName() + "!");
            }
        }

        if(command.getName().equalsIgnoreCase("darkcloud")){
            if(coolDownTime.containsKey(target)){
                target.sendMessage(DwD + ChatColor.GOLD + "You can not use Dark Cloud for another " + coolDownTime.get(target) + " seconds!");
                return true;
            }

            if(isRunning){
                target.sendMessage(DwD + ChatColor.GOLD + "You can only have one Dark Cloud running at a time!");
                return true;
            }

            int num1 = cloudTimer;
            int num2 = 0;

            if(num1 < num2) return true;

            if(!isRunningTime) {
                if (!coolDownTime.containsKey(target)) {
                    final BukkitTask floatingClouds = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.CLOUD.display(0.8F, .2F, 0.8F, 0F, 30, target.getLocation().add(0, 3, 0), 20);
                        }
                    }, 0L, 2L);

                    final BukkitTask dripLava = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.DRIP_LAVA.display(0.5F, 0F, 0.5F, 0.8F, 5, target.getLocation().add(0, 3, 0), 20);
                        }
                    }, 0L, 6L);
                    final BukkitTask dripWater = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.DRIP_WATER.display(0.5F, 0F, 0.5F, 0.8F, 5, target.getLocation().add(0, 3, 0), 20);
                        }
                    }, 0L, 4L);
                    target.sendMessage(
                            DwD + ChatColor.AQUA + "A dark cloud has emerged!\n"
                                    + DwD + ChatColor.GREEN + cloudTimer + " seconds remaining . . ."
                    );
                    taskToCancel = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            if (cloudTimer >= 0) {
                                isRunning = true;
                            } else {
                                isRunning = false;
                            }

                            if (isRunning) {
                                cloudTimer--;

                                floatingClouds.getTaskId();
                                dripLava.getTaskId();
                                dripWater.getTaskId();
                            }

                            if (cloudTimer <= 0 || !target.isOnline() || !isRunning) {

                                floatingClouds.cancel();
                                dripLava.cancel();
                                dripWater.cancel();

                                target.sendMessage(DwD + ChatColor.GOLD + "Your dark cloud has ran out of time");

                                taskToCancel.cancel();

                                isRunning = false;

                                runCoolDown(target);
                            }
                        }
                    }, 0L, 20L);
                }
                return true;
            }
        }
        return true;
    }

    public void runCoolDown(Player player){
        final Player target = player;

        if(!isRunning) {
            coolDownTime.put(target, 600); // cool down for 20 seconds
            taskCoolDownToCancel = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                public void run() {
                    //System.out.println("run()"); // debug run()
                    isRunningTime = true;

                    coolDownTime.put(target, coolDownTime.get(target) - 1);

                    if(coolDownTime.get(target) <= 0) {
                        coolDownTime.remove(target);

                        target.sendMessage(DwD + ChatColor.GOLD + "Your Dark Cloud cooldown has expired.");

                        isRunningTime = false;

                        taskCoolDownToCancel.cancel();
                    }
                }
            }, 0L, 20L);
        }
    }

}