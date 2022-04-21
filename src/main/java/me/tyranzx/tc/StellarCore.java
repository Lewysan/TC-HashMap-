package me.tyranzx.tc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class StellarCore extends JavaPlugin implements CommandExecutor {

     final HashMap<Player, Integer> cooldownTime = new HashMap<>();
     final HashMap<Player, BukkitRunnable> cooldownTask = new HashMap<>();

    String c(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public void onEnable() {
        this.getCommand("tell").setExecutor(this);
        Bukkit.getConsoleSender().sendMessage(c("&aTC activado"));

    }
    public boolean onCommand(CommandSender sender, Command command, String a, String[] arg) {
        int args = arg.length;
        if (!(sender instanceof Player)) {
            sender.sendMessage(c("&cSolo jugadores tienen acceso a este comando."));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("tc.usage")) {
            p.sendMessage(c("&cNo tienes permiso para este comando."));
            return true;
        }
        if (this.cooldownTime.containsKey(p)) {
            p.sendMessage(this.c("&cDebes esperar &f")+cooldownTime.get(p)+c(" segundos &cpara enviar otro mensaje"));
            return true;
        }
        if (command.getName().equalsIgnoreCase("tell")) {
            if (args < 1) {
                p.sendMessage(c("&cUso: /tell <jugador> <mensaje>"));
                return true;
            }
            Player target = Bukkit.getServer().getPlayerExact(arg[0]);
            if (target == null) {
                p.sendMessage(c("&cEste jugador no existe."));
                return true;
            }
            if (args < 2) {
                p.sendMessage(c("&cDebes especificar un mensaje."));
                return true;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 1, c = arg.length; i < c; i++) {
                    sb.append(arg[i]);
                    if (i < c) {
                        sb.append(" ");
                    }
                }
                p.sendMessage(c("&6[&cme &6-> &f") + target.getName() + c("&6] &f") + sb);
                target.sendMessage(c("&6[&f") + p.getName() + c("&6 -> &cme&6] &f") + sb);
                cooldownTime.put(p, 30);
                cooldownTask.put(p, new BukkitRunnable() {
                    public void run() {
                        cooldownTime.put(p, cooldownTime.get(p) - 1);
                        if (cooldownTime.get(p) == 0) {
                            cooldownTime.remove(p);
                            cooldownTask.remove(p);
                            cancel();
                        }
                    }
                });

                cooldownTask.get(p).runTaskTimer(this, 20, 20);
            }
        }
        return false;
    }
}
