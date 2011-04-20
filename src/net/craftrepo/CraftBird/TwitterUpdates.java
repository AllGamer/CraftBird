package net.craftrepo.CraftBird;

import java.util.List;

import twitter4j.*;

/**
 * CraftRepo Bans for Bukkit
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
		if(CraftBird.twitter.getAuthorization().isEnabled())
		{
			while (true)
			{
				net.craftrepo.CraftBird.CraftBird.config.load();
				String[] users = ((String[]) net.craftrepo.CraftBird.CraftBird.config.getProperty("users"));

				for (String s : users)
				{
					try 
					{
						List<Status> statuses;
						statuses = CraftBird.twitter.getFriendsTimeline();
						if (statuses != lastStatuses)
						{
							for (Status status : statuses) 
							{
								if (status.getUser().getName().contains(s))
								{
									CraftBird.notifyPlayers(status.getUser().getName() + ":" + status.getText());
								}
							}
							lastStatuses = statuses;
						}
					} 
					catch (TwitterException e) 
					{
						e.printStackTrace();
						try 
						{
							Thread.sleep(300000);
						}
						catch (InterruptedException e1) 
						{
							e1.printStackTrace();
						}
					}
				}
				try 
				{
					Thread.sleep(300000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try 
			{
				Thread.sleep(300000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}