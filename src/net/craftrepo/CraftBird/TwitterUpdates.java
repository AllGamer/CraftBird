package net.craftrepo.CraftBird;

import java.util.List;

import twitter4j.*;

/**
 * CraftRepo CraftBird for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class TwitterUpdates extends Thread
{
	private CraftBird CraftBird = null;
	public 	List<Status> lastStatuses;

	public TwitterUpdates(CraftBird CraftBird)
	{
		this.CraftBird = CraftBird;
	}

	public void run()
	{
		while (true)
		{
			if (CraftBird.twitter.getAuthorization().isEnabled())
			{
				net.craftrepo.CraftBird.CraftBird.config.load();
				String[] users = ((String[]) net.craftrepo.CraftBird.CraftBird.config.getProperty("users"));
				if (users == null)
				{
					CraftBird.log.warning("[Twitter] No users defined in the config! Turning updates off.");
					CraftBird.updates = false;
					CraftBird.scheduler.cancelAllTasks();
					return;
				} 
				for (String s : users)
				{
					try 
					{
						List<Status> statuses;
						statuses = CraftBird.twitter.getFriendsTimeline();
						if (lastStatuses == null || !statuses.containsAll(lastStatuses))
						{
							for (Status status : statuses) 
							{
								if (status.getUser().getName().contains(s))
								{
									CraftBird.log.info("[Twitter] " + status.getUser().getName() + ":" + status.getText());
									CraftBird.notifyPlayers("[Twitter] " + status.getUser().getName() + ":" + status.getText());
								}
							}
							lastStatuses = statuses;
						}
					} 
					catch (TwitterException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}