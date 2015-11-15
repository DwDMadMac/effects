package net.downwithdestruction.effects.commands;

import net.downwithdestruction.effects.Effects;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/**
 * Created by madmac on 9/19/15.
 *
 * ParticleEffect.FLAME.display(0.5F, 0F, 0F, 0F, 20, target.getLocation().add(1, 1, 0), 20);
 */
public class TestCommandTwo implements CommandExecutor {
    private Effects plugin;
    private String DwD = ChatColor.DARK_RED + "[" + ChatColor.GRAY + "DwD" + ChatColor.DARK_RED + "] ";
    private int numberArgs;
    private boolean isRunning;
    private boolean isRunningTime;
    private BukkitTask taskToCancel;
    private BukkitTask taskCoolDownToCancel;
    private HashMap<Player, Integer> coolDownTime = new HashMap<Player, Integer>();

    public TestCommandTwo(Effects plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(ChatColor.RED + "This command can only be used by players");
            return true;
        }

        if(args.length < 1){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Please enter a time length of 1 - 60 seconds."); //command.getDescription()  <--- This will get command description from plugin .yml
            return true;
        }

        try{
            numberArgs = Integer.valueOf(args[0]);
        }catch (NumberFormatException numberFormatException){
            commandSender.sendMessage(DwD + ChatColor.GOLD + "Please enter a time length of 1 - 60!");
            return true;
        }

        if(numberArgs < 0 || numberArgs > 60){
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
        }

        if(command.getName().equalsIgnoreCase("testcommandtwo")){
            if(coolDownTime.containsKey(target)){
                target.sendMessage(DwD + ChatColor.GOLD + "You can not use this command for another " + coolDownTime.get(target) + " seconds!");
                return true;
            }
            if(isRunning){
                target.sendMessage(DwD + ChatColor.GOLD + "This command was already activated!");
                return true;
            }

            int num1 = numberArgs;
            int num2 = 0;

            if(num1 < num2) return true;

            if(!isRunningTime) {
                if (!coolDownTime.containsKey(target)) {
/*
                    final BukkitTask testOne = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.FLAME.display(0.5F, 0F, 0F, 0F, 20, target.getLocation().add(1, 1, 0), 20);
                        }
                    }, 0L, 2L);

                    final BukkitTask testTwo = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.DRIP_LAVA.display(0.5F, 0F, 0.5F, 0.8F, 5, target.getLocation().add(0, 3, 0), 20);
                        }
                    }, 0L, 6L);

                    final BukkitTask testThree = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            ParticleEffect.DRIP_WATER.display(0.5F, 0F, 0.5F, 0.8F, 5, target.getLocation().add(0, 3, 0), 20);
                        }
                    }, 0L, 4L);
*/
                    /*
                    target.sendMessage(
                            DwD + ChatColor.AQUA + "A dark cloud has emerged!\n"
                                    + DwD + ChatColor.GREEN + numberArgs + " seconds remaining . . ."
                    );
                    */
                    taskToCancel = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        public void run() {
                            if (numberArgs >= 1) {
                                isRunning = true;
                            } else {
                                isRunning = false;
                            }

                            if (isRunning) {
                                numberArgs--;

                                //Effect.AtomEffect;
                                //testOne.getTaskId();
                            }

                            if (numberArgs <= 0 || !target.isOnline() || !isRunning) {

                                //testOne.cancel();

                                target.sendMessage(DwD + ChatColor.GOLD + "Your command has ran out of time");

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
            coolDownTime.put(target, 20); // cool down for 20 seconds
            taskCoolDownToCancel = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                public void run() {
                    //System.out.println("run()"); // debug run()
                    isRunningTime = true;

                    coolDownTime.put(target, coolDownTime.get(target) - 1);

                    if(coolDownTime.get(target) <= 0) {
                        coolDownTime.remove(target);

                        target.sendMessage(DwD + ChatColor.GOLD + "Your command cooldown has expired.");

                        isRunningTime = false;

                        taskCoolDownToCancel.cancel();
                    }
                }
            }, 0L, 20L);
        }
    }
}
