package net.craftrepo.CraftBird;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * CraftRepo gimme for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class CraftBird extends JavaPlugin
{
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public HashMap<String, Integer> items = new HashMap<String, Integer>();
	public final Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	public static String logPrefix = "[CraftBird]";
	private CraftBirdConfiguration confSetup;
	public CraftBird plugin;
	public static Configuration config;
	public static String id = null;
	Twitter twitter = new TwitterFactory().getInstance();
	public RequestToken requestToken;
	public AccessToken accessToken = null;
	BukkitScheduler scheduler = this.getServer().getScheduler();

	public void onEnable() 
	{
		setupPermissions();
		configInit();
		confSetup.setupConfigs();
		if (startOAuth())
		{
			scheduler.scheduleAsyncRepeatingTask(this.plugin, new TwitterUpdates(plugin), 120, 6000);
			log.info(logPrefix + " version " + this.getDescription().getVersion() + " enabled!");
		}
		else
		{
			log.severe(logPrefix + "Couldn't start OAuth with twitter... Disabling.");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() 
	{
		log.info(logPrefix + " version " + this.getDescription().getVersion() + " disabled!");
	}

	public boolean isDebugging(final Player player) 
	{
		if (debugees.containsKey(player)) 
			return debugees.get(player);
		return false;
	}

	public void setDebugging(final Player player, final boolean value) 
	{
		debugees.put(player, value);
	}

	public void configInit()
	{
		getDataFolder().mkdirs();
		config = new Configuration(new File(this.getDataFolder(), "config.yml"));
		confSetup = new CraftBirdConfiguration(this.getDataFolder(), this);
	}

	public void notifyPlayers(String message) 
	{
		for (Player p: getServer().getOnlinePlayers()) 
		{ 
			p.sendMessage("[Twitter] " + message);
		}
	}
	
	public void setupPermissions() 
	{
		Plugin perms = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (CraftBird.Permissions == null) 
		{
			if (perms != null) 
			{
				this.getServer().getPluginManager().enablePlugin(perms);
				CraftBird.Permissions = ((Permissions) perms).getHandler();
				log.info(logPrefix + " version " + pdfFile.getVersion() + " Permissions detected...");
			}
			else 
			{
				log.severe(logPrefix + " version " + pdfFile.getVersion() + " not enabled. Permissions not detected.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public static String strip(String s) 
	{
		String good = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String result = "";
		for ( int i = 0; i < s.length(); i++ ) 
		{
			if ( good.indexOf(s.charAt(i)) >= 0 )
				result += s.charAt(i);
		}
		return result;
	}

	public boolean startOAuth()
	{
		try 
		{
			config.load();
			//String access = config.getProperty("accessToken").toString();
			if(config.getProperty("accessToken") == null)
			{
				twitter.setOAuthConsumer("8Nxl52fGs6sheLOATyklXA", "MahFaucXaRySLARXnxVkUyLOBcDQN1BhzKzAsj9Mc");
				requestToken = twitter.getOAuthRequestToken();
				log.info(logPrefix + " Goto " + requestToken.getAuthorizationURL() + " to authenticate CraftBird.");
				return true;
			}
			else
			{
				twitter.setOAuthConsumer("8Nxl52fGs6sheLOATyklXA", "MahFaucXaRySLARXnxVkUyLOBcDQN1BhzKzAsj9Mc");
				getAccessToken("0");
				return true;
			}
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean getAccessToken(String pin)
	{
		try 
		{
			if (pin.length() > 2) 
			{
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				config.load();
				config.setProperty("accessToken", accessToken.getToken());
				config.setProperty("accessTokenSecret", accessToken.getTokenSecret());
				config.save();
				return true;
			} 
			else 
			{
				config.load();
				AccessToken oldtoken = new AccessToken(config.getProperty("accessToken").toString(),config.getProperty("accessTokenSecret").toString());
				twitter.setOAuthAccessToken(oldtoken);
			}
		}
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean onCommand(CommandSender sender, Command commandArg, String commandLabel, String[] arg) 
	{
		Player player = (Player) sender;
		String command = commandArg.getName().toLowerCase();
		if (command.equalsIgnoreCase("twitterpin")) 
		{
			if (arg.length > 0)
			{
				if (CraftBird.Permissions.has(player, "twitter.tweet"))
				{
					getAccessToken(arg[0]);
					player.sendMessage(logPrefix + " Sucessfully added your pin!");
					return true;
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				}
			}
			else
			{
				player.sendMessage("Correct usage is /twitterpin [pin]");
			}
		}
		if (command.equalsIgnoreCase("tweet"))
		{
			if (arg.length > 0)
			{
				if (CraftBird.Permissions.has(player, "tweet.tweet"))
				{
					String result = "";
					for (String s : arg)
					{
						result += s + " ";
					}
					try 
					{
						twitter.updateStatus(result);
						player.sendMessage(logPrefix + " Sucessfully tweeted!");
						result = "";
					} 
					catch (TwitterException e) 
					{
						e.printStackTrace();
						player.sendMessage("Something wrong happened! Check the console for more info.");
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				}
			}
			else
			{
				player.sendMessage("Correct usage is /tweet [text]");
			}
		}
		return true;
	}
}
