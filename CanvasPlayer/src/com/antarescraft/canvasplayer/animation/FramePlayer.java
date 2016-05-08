package com.antarescraft.canvasplayer.animation;

import java.util.ArrayList;

import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.canvasplayer.main.AnimatedGif;
import com.antarescraft.canvasplayer.main.CPMain;
import com.antarescraft.canvasplayer.main.CanvasPlayer;

public class FramePlayer extends BukkitRunnable
{
	private CanvasPlayer cp;
	private AnimatedGif gif;
	private ArrayList<ItemStack> mapItems;
	
	public FramePlayer(CanvasPlayer cp, AnimatedGif gif)
	{
		this.cp = cp;
		this.gif = gif;
		this.mapItems = gif.getMapItemStacks();
	}

	@Override
	public void run() 
	{
		try
		{			
			if(gif.currentFrame >= gif.getFrames().size() - 1)
			{
				gif.currentFrame = 0;
			}
			
			int mapIndex = cp.getCurrentMapIndex();
			
			if(mapIndex >= gif.getFrameCount() - 1)
			{
				cp.setCurrentMapIndex(0);
				mapIndex = 0;
			}
			
			ItemFrame itemFrame = cp.getItemFrames()[0];
			//ItemStack mapItem = new ItemStack(Material.MAP, 1, gif.getCurrentMapId());
			
			//if(itemFrame.isCustomNameVisible())
			//{
			//	itemFrame.setCustomNameVisible(false);
			//}
			
			//EntityHider hider = new EntityHider(CPMain.PluginInstance, null);

			itemFrame.setItem(mapItems.get(gif.currentFrame));
			
			gif.currentFrame++;
			cp.setCurrentMapIndex(mapIndex + 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			cp.setIsRunning(false);
			cp.gifNames = new ArrayList<String>();
			CPMain.ActiveFramePlayers.remove(cp.getName());
			CPMain.CanvasPlayers.remove(cp.getName());
			this.cancel();
		}
	}
}
