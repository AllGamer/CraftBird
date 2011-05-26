package net.craftrepo.CraftBird;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class CraftBirdConfiguration 
{

	private CraftBird plugin;
	private File folder;
	private final Logger log = Logger.getLogger("Minecraft");
	private String logPrefix;

	public CraftBirdConfiguration(File folder, CraftBird instance) 
	{
		this.folder = folder;
		this.plugin = instance;
		this.logPrefix = CraftBird.logPrefix;
	}

	public void setupConfigs() 
	{
		File config = new File(this.folder, "config.yml");
		if (!config.exists()) 
		{
			try 
			{
				log.info(logPrefix + " - Creating config directory... ");
				log.info(logPrefix + " - Creating config files... ");
				config.createNewFile();
				FileWriter fstream = new FileWriter(config);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("#CraftBird Configuration\n");
				out.write("");
				out.write("#Turn on updates? (true or false)\n");
				out.write("updates: true");
				out.write("\n");
				out.write("#Users to get updates of.\n");
				out.write("users:\n");
				out.write("    - notch\n");
				out.write("    - jeb_\n");
				out.write("    - mollstam\n");
				out.write("    - jahkob\n");
				out.write("    - craftrepo\n");
				out.write("\n");
				out.write("\n");
				out.write("#PLEASE DON'T TOUCH THIS! It auths you with twitter after the first time.\n");
				out.write("accessToken:\n");
				out.write("accessTokenSecret:\n");

				out.close();
				fstream.close();
				log.info(logPrefix + " Make sure to edit your config file!");
			}
			catch (IOException ex) 
			{
				log.severe(logPrefix + " Error creating default Configuration File");
				log.severe(logPrefix + " " + ex);
				this.plugin.getServer().getPluginManager().disablePlugin((Plugin) this);
			}	
		}
	}
}
