package com.antarescraft.canvasplayer.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.canvasplayer.main.AnimatedGif;
import com.antarescraft.canvasplayer.main.CPMain;
import com.antarescraft.canvasplayer.main.CanvasPlayer;
import com.antarescraft.canvasplayer.main.CanvasRenderer;


public class AnimationLauncher extends BukkitRunnable
{
	private CanvasPlayer cp;
	private CommandSender sender;
	
	public AnimationLauncher(CommandSender sender, CanvasPlayer cp)
	{
		this.cp = cp;
		this.sender = sender;
	}

	@Override
	public void run() 
	{
		ArrayList<AnimatedGif> gifs = new ArrayList<AnimatedGif>();
		
		if(cp.gifNames != null)
		{
			for(String gifName : cp.gifNames)
			{
				AnimatedGif gif = null;
				File imageFile = new File("plugins/CanvasPlayer/images/" + gifName + ".dat");

				if(imageFile.exists())
				{
					try
					{
						FileInputStream fileIn = new FileInputStream(imageFile);
						ObjectInputStream in = null;
						
						if(imageFile.length() > 0)
						{				
							in = new ObjectInputStream(fileIn);
							gif = (AnimatedGif) in.readObject();
							in.close();		
							
							gifs.add(gif);
						}
					}
					catch(Exception e)
					{
						if(sender != null)
						{
							sender.sendMessage(ChatColor.RED + "Error processing the image");
						}
						break;
					}
				}
				else
				{
					if(sender != null)
					{
						sender.sendMessage(ChatColor.RED + "One or more of those image ids do not exist.");
					}
					break;
				}
			}
			
			for(AnimatedGif gif : gifs)
			{
				CPMain.Gifs.put(gif.getName(), gif);
			}

			start(gifs);
		}
	}
	
	public void start(ArrayList<AnimatedGif> gifs)
	{	
		if(gifs != null && gifs.size() > 0)
		{		
			FramePlayer framePlayer = new FramePlayer(cp, gifs.get(0));	
			
			FramePlayer fp = CPMain.ActiveFramePlayers.get(cp.getName());
			
			//stop any currently running animation on the LEDArray before you start another
			if(fp != null)
			{
				CPMain.ActiveFramePlayers.remove(cp.getName());
				fp.cancel();
			}
			
			cp.setIsRunning(true);
			cp.currentGifIndex = 0;
			
			for(int i = 0; i < gifs.get(0).mapIds.size(); i++)
			{
				short mapId = gifs.get(0).mapIds.get(i);
				
				@SuppressWarnings("deprecation")
				MapView map = Bukkit.getMap(mapId);
				
				for(MapRenderer r : map.getRenderers())
				{
					map.removeRenderer(r);
				}
				
				CanvasRenderer cr = new CanvasRenderer(gifs.get(0), i);
				cr.initialize(map);
				
				map.addRenderer(cr);
			}
			
			CPMain.ActiveFramePlayers.put(cp.getName(), framePlayer);
			framePlayer.runTaskTimerAsynchronously(CPMain.PluginInstance, 0, 2);
	
			if(sender != null && gifs.get(0) != null)
			{
				sender.sendMessage(ChatColor.AQUA + "'" + gifs.get(0).getName() + "'" + ChatColor.GOLD + " started!");
			}
		}
		else
		{
			if(sender != null)
			{
				sender.sendMessage(ChatColor.RED + "Image id does not exist");
			}
		}
	}
}