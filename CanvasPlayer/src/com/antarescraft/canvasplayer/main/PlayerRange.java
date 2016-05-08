package com.antarescraft.canvasplayer.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerRange extends BukkitRunnable
{

	@Override
	public void run() 
	{		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			for(CanvasPlayer cp : CPMain.CanvasPlayers.values())
			{
				if(player.getLocation().distance(cp.getLocation()) <= 30)
				{					
					for(AnimatedGif gif : cp.getAnimatedGifs())
					{
						for(MapView map : gif.getMapViews())
						{
							player.sendMap(map);
						}
					}
				}
			}
		}
	}
	
}
