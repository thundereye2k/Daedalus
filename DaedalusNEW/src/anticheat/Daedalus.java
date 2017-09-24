package anticheat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import anticheat.commands.CommandManager;
import anticheat.data.DataManager;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.events.EventJoinQuit;
import anticheat.events.EventPlayerAttack;
import anticheat.events.EventPlayerMove;
import anticheat.events.EventTick;
import anticheat.events.TickEvent;
import anticheat.utils.Color;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Daedalus extends JavaPlugin {

	private static ChecksManager checksmanager;
	private static DataManager data;
	private static Daedalus Daedalus;
	private static CommandManager commandManager;
	BufferedWriter bw = null;
	File file = new File(getDataFolder(), "JD.txt");


	public static DataManager getData() {
		return data;
	}

	public static Daedalus getAC() {
		return Daedalus;
	}

	public ChecksManager getChecks() {
		return checksmanager;
	}

	public ChecksManager getchecksmanager() {
		return checksmanager;
	}
	
	public String getPrefix() {
		return Color.translate(getConfig().getString("Prefix"));
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void onEnable() {
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d------------------------------------------"));
		Daedalus = this;
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Main class!"));
		checksmanager = new ChecksManager(this);
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded checks!"));
		commandManager = new CommandManager();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded commands!"));
		Daedalus.data = new DataManager();
		saveDefaultConfig();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Configuration!"));
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded players data's!"));
		commandManager.init();
		checksmanager.init();
		for(Checks check : checksmanager.getDetections()) {
			if(getConfig().contains("checks." + check.getName())) {
				check.setState(getConfig().getBoolean("checks." + check.getName()));
			} else {
				getConfig().set("checks." + check.getName(), check.getState());
			}
		}
		registerEvents();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Registered events!"));
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Made Daedalus file!"));

		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Made JudgementDay txt file!"));
				e.printStackTrace();
			}
		}
		
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Daedalus!"));
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d------------------------------------------"));

	}
	
	public void onDisable() {
		for(Checks check : checksmanager.getDetections()) {
			getConfig().set("checks." + check.getName(), check.getState());
		}
	}

	public void clearVLS() {
		for(Player online : Bukkit.getOnlinePlayers()) {
			data.getProfil(online).clearDetections();
		}
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventPlayerMove(), this);
		pm.registerEvents(new EventPlayerAttack(), this);
		pm.registerEvents(new EventTick(), this);
		pm.registerEvents(new EventJoinQuit(), this);

		data.loaddata();

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				clearVLS();
			}

		}, 0L, 1200L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent());

			}

		}, 0L, 20L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.getCommandManager().CmdHandler(sender, label, args);
		return true;
	}

}
